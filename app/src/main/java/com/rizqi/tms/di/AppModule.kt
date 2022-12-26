package com.rizqi.tms.di

import android.content.Context
import android.content.SharedPreferences
import com.rizqi.tms.TMSPreferences
import com.rizqi.tms.room.TMSDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTMSDatabase(
        @ApplicationContext context: Context
    ) : TMSDatabase = TMSDatabase.getDatabase(context)
}