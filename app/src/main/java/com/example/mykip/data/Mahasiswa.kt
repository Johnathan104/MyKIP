package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_mahasiswa")
data class Mahasiswa(
    @PrimaryKey(autoGenerate = false)
    val nim: String,
    val nama: String,
    val jurusan: String,
    val photoResId: Int
)