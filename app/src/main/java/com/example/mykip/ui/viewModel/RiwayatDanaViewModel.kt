package com.example.mykip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.RiwayatDana
import com.example.mykip.repository.RiwayatDanaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RiwayatDanaViewModel(
    private val repository: RiwayatDanaRepository
) : ViewModel() {

    private val _riwayatList = MutableStateFlow<List<RiwayatDana>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatDana>> = _riwayatList

    // 🔹 Load ALL riwayat (untuk daftar anak, dashboard admin, dll)
    fun getAll() {
        viewModelScope.launch {
            _riwayatList.value = repository.get()
        }
    }

    fun getByNim(nim:String, onResult: (List<RiwayatDana>,) -> Unit,  ) {
        viewModelScope.launch {
            onResult(repository.getByNim(nim))
        }
    }

    // 🔹 Insert + refresh list
    fun insert(riwayatDana: RiwayatDana) {
        viewModelScope.launch {
            repository.insert(riwayatDana)
            getAll()   // refresh setelah insert
        }
    }

    // 🔹 Delete + refresh list
    fun delete(riwayatDana: RiwayatDana) {
        viewModelScope.launch {
            repository.delete(riwayatDana)
            getAll()   // refresh setelah delete
        }
    }
}
