package com.rizqi.tms.di

import android.content.Context
import android.content.SharedPreferences
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences
import com.rizqi.tms.room.TMSDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTMSDatabase(
        @ApplicationContext context: Context
    ) : TMSDatabase = TMSDatabase.getDatabase(context)

    @Singleton
    @Provides
    @FirebaseWebClientId
    fun provideFirebaseWebClientId(
        @ApplicationContext context: Context
    ) : String = context.getString(R.string.default_web_client_id)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FirebaseWebClientId
}