package com.example.mykip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.RiwayatDana
import com.example.mykip.repository.RiwayatDanaRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatDanaViewModel(
    private val repository: RiwayatDanaRepository
) : ViewModel() {
    fun getTodayDate(): Timestamp {
        return Timestamp(Date()) // Create a new Timestamp from the current Date
    }

    fun tambahRiwayat(
        nim: String,
        jumlah: Int,
        keterangan: String,
        jenis: String,
        goingIn:Boolean =false,
    ) {
        val data = hashMapOf(
            "nim" to nim,
            "jumlah" to jumlah,
            "keterangan" to keterangan,
            "jenis" to jenis,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("riwayat")
            .add(data)
    }

    private val _riwayatList = MutableStateFlow<List<RiwayatDana>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatDana>> = _riwayatList

    fun getAll() {
        viewModelScope.launch {
            _riwayatList.value = repository.get()
        }
    }

    fun getByNim(nim: String, onResult: (List<RiwayatDana>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getByNim(nim))
        }
    }

    fun insertRiwayat(nim: String, jumlah: Int, masuk: Boolean, keterangan: String) {
        viewModelScope.launch {
            repository.insert(
                RiwayatDana(
                    nim = nim,
                    tanggal = getTodayDate(),
                    goingIn = masuk,
                    jumlah = jumlah,
                    keterangan = keterangan
                )
            )
            getAll()
        }
    }



    fun delete(riwayatDana: RiwayatDana) {
        viewModelScope.launch {
            repository.delete(riwayatDana)
            getAll()
        }
    }
}
