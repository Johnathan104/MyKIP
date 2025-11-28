package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_mahasiswa")
data class Mahasiswa(
    @PrimaryKey
    val nim: String = "",
    val kuliah: String = "",
    val semester: Int= 1,
    val jenjang: String="",
    val nama: String = "",
    val jurusan: String = "",
    val photoResId: Int = 0
)
