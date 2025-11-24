package com.example.mykip.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mykip.repository.MahasiswaRepository
import com.example.mykip.viewmodel.MahasiswaViewModel

class MahasiswaViewModelFactory(
    private val repository: MahasiswaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MahasiswaViewModel::class.java)) {
            return MahasiswaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
