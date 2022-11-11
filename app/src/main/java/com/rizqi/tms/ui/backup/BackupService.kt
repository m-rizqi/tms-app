package com.rizqi.tms.ui.backup

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.getFirebaseUserId
import com.rizqi.tms.TMSPreferences.Companion.setLastBackupDate
import com.rizqi.tms.di.NotificationModule
import com.rizqi.tms.repository.ItemRepository
import com.rizqi.tms.repository.UnitRepository
import com.rizqi.tms.utility.BACKUP_NOTIFICATION_ID
import com.rizqi.tms.utility.getBitmapFromPath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class BackupService : Service() {

    @Inject
    @NotificationModule.BackupNotificationCompatBuilder
    lateinit var backupNotificationBuilder : NotificationCompat.Builder
    @Inject
    lateinit var notificationManager: NotificationManagerCompat
    @Inject
    lateinit var itemRepository: ItemRepository
    @Inject
    lateinit var unitRepository: UnitRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            BACKUP_NOTIFICATION_ID,
            backupNotificationBuilder.build()
        )
        backupDatabase()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

//    private fun backupDatabase(){
//        val dbFile = applicationContext.getDatabasePath(TMS_DATABASE)
//        val storage = Firebase.storage
//        val backupRef = storage.reference.child("backup/${getFirebaseUserId()}.db")
//        val uploadTask = backupRef.putFile(dbFile.toUri())
//        uploadTask.addOnFailureListener {
//            stopForeground(true)
//            stopSelf()
//            notificationManager.notify(
//                BACKUP_NOTIFICATION_ID,
//                backupNotificationBuilder
//                    .setContentTitle(getString(R.string.backup_failed))
//                    .setContentText("")
//                    .clearActions()
//                    .setProgress(0,0, false)
//                    .build()
//            )
//        }.addOnSuccessListener {
//            stopForeground(true)
//            stopSelf()
//            notificationManager.notify(
//                BACKUP_NOTIFICATION_ID,
//                backupNotificationBuilder
//                    .setContentTitle(getString(R.string.backup_success))
//                    .clearActions()
//                    .setContentText("")
//                    .setProgress(0,0, false)
//                    .build()
//            )
//        }.addOnProgressListener {snapshot ->
//            val transferred = snapshot.bytesTransferred
//            val total = snapshot.totalByteCount
//            val progress = (100 * transferred/total).toInt()
//            notificationManager.notify(
//                BACKUP_NOTIFICATION_ID,
//                backupNotificationBuilder
//                    .setContentTitle(getString(R.string.uploading))
//                    .setContentText("${progress}/100")
//                    .setProgress(100, progress, true)
//                    .build()
//            )
//        }
//    }

    private fun backupDatabase(){
        val database = Firebase.database
        val backupRef = database.getReference("backup/${getFirebaseUserId()}")
        CoroutineScope(Dispatchers.Main).launch {
            itemRepository.getAllItem().collect() {
                notificationManager.notify(
                    BACKUP_NOTIFICATION_ID,
                    backupNotificationBuilder
                        .setContentTitle(getString(R.string.uploading_items))
                        .clearActions()
                        .setContentText("")
                        .setProgress(0,0, false)
                        .build()
                )
                val itemTask = backupRef.child("items").setValue(it.map { it1 -> it1.toNetworkItem() })
                it.forEachIndexed { index, itemWithPrices ->
                    itemWithPrices.item.imagePath?.let { path ->
                        val storage = Firebase.storage
                        val storageRef = storage.reference.child("backup/${getFirebaseUserId()}/${path}")
                        getBitmapFromPath(path)?.let {bitmap ->
                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data: ByteArray = baos.toByteArray()
                            val uploadTask = storageRef.putBytes(data).await()
                        }
                    }
                    val progress = index / it.size * 100
                    notificationManager.notify(
                        BACKUP_NOTIFICATION_ID,
                        backupNotificationBuilder
                            .setContentTitle(getString(R.string.uploading_image))
                            .setContentText("${index}/${it.size}")
                            .setProgress(100, progress, true)
                            .build()
                    )
                }
                itemTask.addOnCompleteListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        notificationManager.notify(
                            BACKUP_NOTIFICATION_ID,
                            backupNotificationBuilder
                                .setContentTitle(getString(R.string.uploading_units))
                                .clearActions()
                                .setContentText("")
                                .setProgress(0,0, false)
                                .build()
                        )
                        unitRepository.getAll().collect() {units ->
                            backupRef.child("units").setValue(units)
                                .addOnCompleteListener {
                                    setLastBackupDate()
                                    stopForeground(true)
                                    stopSelf()
                                    notificationManager.notify(
                                        BACKUP_NOTIFICATION_ID,
                                        backupNotificationBuilder
                                            .setContentTitle(getString(R.string.backup_success))
                                            .clearActions()
                                            .setContentText("")
                                            .setProgress(0,0, false)
                                            .build()
                                    )
                                }
                        }
                    }
                }
            }
        }
    }

}