package com.rizqi.tms.data.datasource.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rizqi.tms.data.datasource.room.TMSDatabase

class InMemoryTMSDatabase {

    companion object {
        fun getDatabase(): TMSDatabase {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val db = Room.inMemoryDatabaseBuilder(
                context, TMSDatabase::class.java
            ).build()
            return db
        }
    }
}