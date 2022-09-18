package com.rizqi.tms.ui.backup

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.getFirebaseUserId
import com.rizqi.tms.di.NotificationModule
import com.rizqi.tms.utility.BACKUP_NOTIFICATION_ID
import com.rizqi.tms.utility.TMS_DATABASE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupService : Service() {

    @Inject
    @NotificationModule.BackupNotificationCompatBuilder
    lateinit var backupNotificationBuilder : NotificationCompat.Builder
    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(BACKUP_NOTIFICATION_ID, backupNotificationBuilder.build())
        backupDatabase()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun backupDatabase(){
        val dbFile = applicationContext.getDatabasePath(TMS_DATABASE)
        val storage = Firebase.storage
        val backupRef = storage.reference.child("backup/${getFirebaseUserId()}.db")
        val uploadTask = backupRef.putFile(dbFile.toUri())
        uploadTask.addOnFailureListener {
            stopForeground(false)
            stopSelf()
            notificationManager.notify(
                BACKUP_NOTIFICATION_ID,
                backupNotificationBuilder
                    .setContentTitle(getString(R.string.backup_failed))
                    .setContentText("")
                    .clearActions()
                    .setProgress(0,0, false)
                    .build()
            )
        }.addOnSuccessListener {
            stopForeground(false)
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
        }.addOnProgressListener {snapshot ->
            val transferred = snapshot.bytesTransferred
            val total = snapshot.totalByteCount
            val progress = (100 * transferred/total).toInt()
            notificationManager.notify(
                BACKUP_NOTIFICATION_ID,
                backupNotificationBuilder
                    .setContentTitle(getString(R.string.uploading))
                    .setContentText("${progress}/100")
                    .setProgress(100, progress, true)
                    .build()
            )
        }
    }

}