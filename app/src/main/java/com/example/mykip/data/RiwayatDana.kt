package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_dana")
data class RiwayatDana(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nim: String,
    val tanggal: String,
    val goingIn: Boolean,
    val jumlah: Int,
    val keterangan: String
)
