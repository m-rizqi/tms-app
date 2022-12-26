package com.rizqi.tms.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rizqi.tms.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User) : Long

    @Query("SELECT * FROM User WHERE id =:id")
    fun getUser(id : Long): Flow<User>

    @Query("DELETE FROM User")
    suspend fun deleteAllUser()
}