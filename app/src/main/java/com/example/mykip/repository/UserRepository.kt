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
    suspend fun getUserByNim(nim: String) = userDao.getUserByNim(nim)
    suspend fun updateBalance(nim: String, balance: Int) = userDao.updateBalance(nim, balance)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    // ➕ Tambahkan ini
    suspend fun setAdmin(nim: String, isAdmin: Boolean) {
        userDao.setAdmin(nim, isAdmin)
    }
}


