package org.whynot.kipku.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.whynot.kipku.repository.OrangTuaRepository

class OrangTuaViewModelFactory(
    private val repository: OrangTuaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrangTuaViewModel::class.java)) {
            return OrangTuaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
