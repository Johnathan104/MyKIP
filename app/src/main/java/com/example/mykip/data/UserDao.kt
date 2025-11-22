package com.example.mykip.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query(value = "SELECT * FROM tbl_users WHERE nim = :nim AND password = :password")
    suspend fun login(nim: String, password: String): User?
    @Query (value = "SELECT * FROM tbl_users WHERE email = :email OR nim = :nim")
    suspend fun getUserByEmailorNim(email: String, nim:String): User?

    @Query(value = "SELECT * FROM tbl_users")
    suspend fun getAllUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}