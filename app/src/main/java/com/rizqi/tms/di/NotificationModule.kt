package com.rizqi.tms.di

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.rizqi.tms.R
import com.rizqi.tms.utility.BACKUP_NOTIFICATION_CHANNEL_ID
import com.rizqi.tms.utility.BACKUP_NOTIFICATION_CHANNEL_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    @MainNotificationCompatBuilder
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        channelId : String = ""
    ) : NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
    }

    @Singleton
    @Provides
    @BackupNotificationCompatBuilder
    fun provideBackupNotificationBuilder(
        @ApplicationContext context: Context
    ) : NotificationCompat.Builder {
        val notificationBuilder = provideNotificationBuilder(context, BACKUP_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(BACKUP_NOTIFICATION_CHANNEL_NAME)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }
        return notificationBuilder
    }

    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainNotificationCompatBuilder

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BackupNotificationCompatBuilder
}