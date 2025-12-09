package com.example.mykip.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.whynot.kipku.ui.viewModel.RiwayatDanaViewModel
import org.whynot.kipku.repository.RiwayatDanaRepository

class RiwayatDanaViewModelFactory(
    private val repository: RiwayatDanaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiwayatDanaViewModel::class.java)) {
            return RiwayatDanaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
