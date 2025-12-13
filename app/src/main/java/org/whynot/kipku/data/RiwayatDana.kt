package org.whynot.kipku.data

import com.google.firebase.Timestamp

data class RiwayatDana(
    var id: String = "",
    val nim: String="",
    val jenis: String = "", // <--- ini penting
    val status: String = "",         // pending / approved / rejected
    val tanggal: Timestamp = Timestamp.now(),
    val timestamp: Long = 0L, // ← PENTING
    val goingIn: Boolean=false,
    val jumlah: Int=0,
    val semester: Int?= null,
    val keterangan: String="",
    val bukti_transfer: String? = null // <-- SESUAI FIRESTORE

)
