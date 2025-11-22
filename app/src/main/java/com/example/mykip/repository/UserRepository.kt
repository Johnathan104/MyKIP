package com.example.mykip.repository

import com.example.mykip.data.User
import com.example.mykip.data.UserDao

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun register(user: User): Boolean {
        val existing = userDao.getUserByEmailorNim(user.email, user.nim)

        return if (existing != null) {
            false
        } else {
            userDao.insertUser(user)
            true
        }
    }
    suspend fun login(nim: String, password: String) = userDao.login(nim, password)
    suspend fun getAllUsers() = userDao.getAllUsers()
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
}

