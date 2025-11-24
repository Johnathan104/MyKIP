package com.example.mykip.data

import androidx.room.*

@Dao
interface RiwayatDanaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatDana)

    @Query("SELECT * FROM tbl_dana WHERE nim = :nim")
    suspend fun getRiwayatByNim(nim: String): List<RiwayatDana>
    @Query("SELECT * FROM tbl_dana")
    suspend fun getRiwayat(): List<RiwayatDana>

    @Delete
    suspend fun deleteRiwayat(riwayat: RiwayatDana)
}
