package com.example.mykip.repository

import com.example.mykip.data.OrangTua
import com.example.mykip.data.OrangTuaDao

class OrangTuaRepository(private val dao: OrangTuaDao) {

    suspend fun insert(orangTua: OrangTua) {
        dao.insert(orangTua)
    }

    suspend fun getOrangTuaByEmail(email: String): OrangTua? {
        return dao.getByEmail(email)
    }
}
