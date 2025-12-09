package org.whynot.kipku.data

import androidx.room.Entity

@Entity(tableName = "tbl_anak")
data class Anak(
    val id: String,
    val nama: String,
    val kelas: String,
    val danaTersisa: Int,
    val danaTerpakai: Int,
    val photoResId: Int
)
