package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orang_tua")
data class OrangTua(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val email: String,
    val anakNim: String? = null
)
