package com.example.mykip.repository

import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.MahasiswaDao

class MahasiswaRepository(
    private val dao: MahasiswaDao
) {
    suspend fun insert(mahasiswa: Mahasiswa) {
        dao.insertMahasiswa(mahasiswa)
    }

    suspend fun getAll(): List<Mahasiswa> {
        return dao.getAllMahasiswa()
    }

    suspend fun getByNim(nim: String): Mahasiswa? {
        return dao.getMahasiswaByNim(nim)
    }

    suspend fun update(mahasiswa: Mahasiswa) {
        dao.updateMahasiswa(mahasiswa)
    }

    suspend fun delete(mahasiswa: Mahasiswa) {
        dao.deleteMahasiswa(mahasiswa)
    }
}
