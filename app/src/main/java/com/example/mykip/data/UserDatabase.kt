package com.example.mykip.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.mykip.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun contohMahasiswa(): List<Mahasiswa> = listOf(
    Mahasiswa(
        nim = "412022005",
        nama = "Brino Alfaro",
        jurusan = "IPA",
        photoResId = R.drawable.avatar1
    ),
    Mahasiswa(
        nim = "412022006",
        nama = "Vanda Christie",
        jurusan = "IPS",
        photoResId = R.drawable.avatar2
    )
)

fun contohRiwayat(): List<RiwayatDana> = listOf(
    RiwayatDana(
        id = 0,
        nim = "412022005",
        tanggal = "2025-01-02",
        goingIn = false,
        jumlah = 150000,
        keterangan = "Beli susu"
    ),
    RiwayatDana(
        id = 0,
        nim = "412022005",
        tanggal = "2025-01-04",
        goingIn = true,
        jumlah = 200000,
        keterangan = "Pemasukan tambahan"
    ),
    RiwayatDana(
        id = 0,
        nim = "412022006",
        tanggal = "2025-01-10",
        goingIn = false,
        jumlah = 150000,
        keterangan = "Pengeluaran kegiatan"
    )
)

// Tambahkan OrangTua di sini
@Database(
    entities = [
        RiwayatDana::class,
        Mahasiswa::class,
        OrangTua::class           // ← WAJIB DITAMBAHKAN
    ],
    version = 10,                  // ← NAIKKAN VERSION SETIAP UBAH ENTITY
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {

    abstract fun riwayatDanaDao(): RiwayatDanaDao
    abstract fun mahasiswaDao(): MahasiswaDao
    abstract fun orangTuaDao(): OrangTuaDao     // ← sudah benar

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "kip_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())   // ← callback sederhana
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    val database = INSTANCE ?: return@launch

                    val mahasiswaDao = database.mahasiswaDao()
                    val riwayatDao = database.riwayatDanaDao()

                    // Insert sample mahasiswa
                    contohMahasiswa().forEach { m ->
                        mahasiswaDao.insertMahasiswa(m)
                    }

                    // Insert sample riwayat dana
                    contohRiwayat().forEach { r ->
                        riwayatDao.insertRiwayat(r)
                    }
                }
            }
        }
    }
}

// simple background thread
fun ioThread(f: () -> Unit) {
    Thread(f).start()
}
