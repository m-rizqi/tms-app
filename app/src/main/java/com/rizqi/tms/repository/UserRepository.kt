package com.rizqi.tms.repository

import com.rizqi.tms.model.User
import com.rizqi.tms.room.TMSDatabase
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: TMSDatabase
) {
    private val userDao = db.userDao()

    suspend fun insertUser(user: User) = userDao.insert(user)

    fun getUser(id : Long) = userDao.getUser(id)
}