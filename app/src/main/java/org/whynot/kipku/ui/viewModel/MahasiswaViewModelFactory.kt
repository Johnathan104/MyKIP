package org.whynot.kipku.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.whynot.kipku.repository.MahasiswaRepository
import org.whynot.kipku.ui.viewModel.MahasiswaViewModel

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
