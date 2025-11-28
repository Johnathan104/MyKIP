package com.example.mykip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrangTuaDao {

    @Insert
    suspend fun insert(orangTua: OrangTua)

    @Query("SELECT * FROM orang_tua WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): OrangTua?
}
