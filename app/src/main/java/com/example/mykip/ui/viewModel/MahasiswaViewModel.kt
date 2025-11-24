package com.example.mykip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.repository.MahasiswaRepository
import kotlinx.coroutines.launch

class MahasiswaViewModel(
    private val repository: MahasiswaRepository
) : ViewModel() {

    fun insert(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            repository.insert(mahasiswa)
        }
    }

    fun getAll(onResult: (List<Mahasiswa>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAll())
        }
    }

    fun getByNim(nim: String, onResult: (Mahasiswa?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getByNim(nim))
        }
    }

    fun update(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            repository.update(mahasiswa)
        }
    }

    fun delete(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            repository.delete(mahasiswa)
        }
    }
}
