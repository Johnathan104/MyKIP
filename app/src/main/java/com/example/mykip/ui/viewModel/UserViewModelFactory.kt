package com.example.mykip.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mykip.data.SessionManager
import com.example.mykip.repository.UserRepository

// In your ui/viewModel/UserViewModel.kt file (or wherever the factory is)

class UserViewModelFactory(
    private val repository: UserRepository,
    private val sessionManager: SessionManager // <-- ADD THIS
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository, sessionManager) as T // <-- PASS IT HERE
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
