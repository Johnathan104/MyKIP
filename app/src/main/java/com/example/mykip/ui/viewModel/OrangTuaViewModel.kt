package com.example.mykip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.OrangTua
import com.example.mykip.repository.OrangTuaRepository
import kotlinx.coroutines.launch

class OrangTuaViewModel(
    private val repository: OrangTuaRepository
) : ViewModel() {

    fun insert(orangTua: OrangTua) {
        viewModelScope.launch {
            repository.insert(orangTua)
        }
    }
}

