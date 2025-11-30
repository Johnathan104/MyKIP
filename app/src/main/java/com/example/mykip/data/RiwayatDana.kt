package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

data class RiwayatDana(
    val id: Int = 0,
    val nim: String="",
    val tanggal: Timestamp = Timestamp.now(),
    val goingIn: Boolean=false,
    val jumlah: Int=0,
    val keterangan: String=""
)
