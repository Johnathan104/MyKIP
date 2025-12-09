package org.whynot.kipku.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.whynot.kipku.data.SessionManager
import org.whynot.kipku.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore

// In your ui/viewModel/UserViewModel.kt file (or wherever the factory is)

class UserViewModelFactory(
    private val repository: UserRepository,
    private val sessionManager: SessionManager,
    private val firestore: FirebaseFirestore// <-- ADD THIS
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository, firestore,  sessionManager) as T // <-- PASS IT HERE
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
