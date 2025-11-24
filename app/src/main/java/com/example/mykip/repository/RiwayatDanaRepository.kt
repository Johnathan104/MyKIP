package com.example.mykip.repository

import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.RiwayatDanaDao

class RiwayatDanaRepository(
    private val dao: RiwayatDanaDao
) {

    suspend fun insert(riwayatDana: RiwayatDana) {
        dao.insertRiwayat(riwayatDana)
    }

    suspend fun get(): List<RiwayatDana> {
        return dao.getRiwayat()
    }

    suspend fun getByNim(nim: String): List<RiwayatDana> {
        return dao.getRiwayatByNim(nim)
    }

    suspend fun delete(riwayatDana: RiwayatDana) {
        dao.deleteRiwayat(riwayatDana)
    }
}
