package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "tbl_mahasiswa")
data class Mahasiswa(
    @PrimaryKey
    val nim: String = "",
    val kuliah: String = "",
    val semester: Int= 1,
    val jenjang: String="",
    val nama: String = "",
    val jurusan: String = "",
    val alamat:String? = null,
    val tanggalLahir: Timestamp? = null,
    val emailWali:String? = null,

    val photoResId: Int = 0
)
