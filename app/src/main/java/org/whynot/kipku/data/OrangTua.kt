package org.whynot.kipku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orang_tua")
data class OrangTua(
    @PrimaryKey
    val email: String= "",
    val nama: String= "",
    val anakNim: String = ""
)
