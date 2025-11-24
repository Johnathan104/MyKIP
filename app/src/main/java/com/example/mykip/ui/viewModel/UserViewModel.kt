package com.example.mykip.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.User
import com.example.mykip.repository.UserRepository
import kotlinx.coroutines.launch


class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    // Simpan user yang login
    var loggedInUser: User? by mutableStateOf(null)
        private set

    /** Reset state ke default */
    fun resetState() {
        uiState = UiState()
    }

    fun login(nim: String, password: String) {
        // Validasi input
        if (nim.isBlank() || password.isBlank()) {
            uiState = UiState(
                isLoading = false,
                isSuccess = false,
                message = "NIM and Password cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            uiState = UiState(isLoading = true)

            val result = repository.login(nim = nim, password = password)

            uiState = if (result != null) {
                loggedInUser = result // simpan user yang login
                UiState(
                    isLoading = false,
                    isSuccess = true,
                    message = "Login successful"
                )
            } else {
                UiState(
                    isLoading = false,
                    isSuccess = false,
                    message = "Login failed: NIM atau password salah"
                )
            }
        }
    }

    fun register(nim: String, email: String, password: String) {
        // Validasi input
        if (nim.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = UiState(
                isLoading = false,
                isSuccess = false,
                message = "All fields are required"
            )
            return
        }

        viewModelScope.launch {
            uiState = UiState(isLoading = true)

            val result = repository.register(
                User(nim = nim, email = email, password = password)
            )

            uiState = if (result) {
                UiState(
                    isLoading = false,
                    isSuccess = true,
                    message = "Registration successful"
                )
            } else {
                UiState(
                    isLoading = false,
                    isSuccess = false,
                    message = "Registration failed: NIM atau email sudah digunakan"
                )
            }
        }
    }

    /** Logout user */
    fun logout() {
        loggedInUser = null
        resetState()
    }
}
