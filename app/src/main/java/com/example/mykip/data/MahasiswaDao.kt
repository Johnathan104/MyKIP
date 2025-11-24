package com.example.mykip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface MahasiswaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMahasiswa(mahasiswa: Mahasiswa)

    @Query("SELECT * FROM tbl_mahasiswa")
    suspend fun getAllMahasiswa(): List<Mahasiswa>

    @Query("SELECT * FROM tbl_mahasiswa WHERE nim = :nim")
    suspend fun getMahasiswaByNim(nim: String): Mahasiswa?

    @Update
    suspend fun updateMahasiswa(mahasiswa: Mahasiswa)

    @Delete
    suspend fun deleteMahasiswa(mahasiswa: Mahasiswa)
}
