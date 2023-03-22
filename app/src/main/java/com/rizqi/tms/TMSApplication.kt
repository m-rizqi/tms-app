package com.rizqi.tms

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

private const val BACKUP_NOTIFICATION_CHANNEL_ID = "backup_notification"
private const val BACKUP_NOTIFICATION_CHANNEL_NAME = "Backup"

@HiltAndroidApp
class TMSApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val backupChannel = NotificationChannel(
                BACKUP_NOTIFICATION_CHANNEL_ID,
                BACKUP_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.backup_notification_channel_description)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(backupChannel)
        }
    }
}