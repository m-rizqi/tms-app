package com.rizqi.tms.ui.backup

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.getBackupSchedule
import com.rizqi.tms.TMSPreferences.Companion.getFirebaseUserId
import com.rizqi.tms.TMSPreferences.Companion.isBackupWithImage
import com.rizqi.tms.TMSPreferences.Companion.setLastBackupDate
import com.rizqi.tms.TMSPreferences.Companion.setNextBackupDate
import com.rizqi.tms.di.NotificationModule
import com.rizqi.tms.model.BackupSchedule
import com.rizqi.tms.model.BackupSchedule.*
import com.rizqi.tms.repository.ItemRepository
import com.rizqi.tms.repository.UnitRepository
import com.rizqi.tms.utility.BACKUP_NOTIFICATION_ID
import com.rizqi.tms.utility.getBitmapFromPath
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.Calendar
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
                if (isBackupWithImage()){
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
                }
                itemTask.addOnCompleteListener {task ->
                    if (task.isSuccessful.not()){
                        notificationManager.notify(
                            BACKUP_NOTIFICATION_ID,
                            backupNotificationBuilder
                                .setContentTitle(getString(R.string.backup_failed))
                                .clearActions()
                                .setContentText("")
                                .setProgress(0,0, false)
                                .build()
                        )
                    }
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
                                .addOnCompleteListener {task2 ->
                                    setLastBackupDate()
                                    stopForeground(true)
                                    stopSelf()
                                    if (task2.isSuccessful.not()){
                                        notificationManager.notify(
                                            BACKUP_NOTIFICATION_ID,
                                            backupNotificationBuilder
                                                .setContentTitle(getString(R.string.backup_failed))
                                                .clearActions()
                                                .setContentText("")
                                                .setProgress(0,0, false)
                                                .build()
                                        )
                                    }else{
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

                                    val calendar = Calendar.getInstance()
                                    calendar.add(
                                        when(getBackupSchedule()){
                                            EVERY_DAY -> Calendar.DATE
                                            EVERY_WEEK -> Calendar.DATE
                                            EVERY_MONTH -> Calendar.MONTH
                                            NEVER ->  Calendar.YEAR
                                        },
                                        if (getBackupSchedule() != EVERY_WEEK) 1 else 7
                                    )
                                    setNextBackupDate(calendar.timeInMillis)
                                }
                        }
                    }
                }
            }
        }
    }

}