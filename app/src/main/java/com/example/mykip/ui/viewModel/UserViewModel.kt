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
    fun login (nim:String, password: String){
        viewModelScope.launch {
            uiState = UiState(isLoading = true)

            val result = repository.login(nim = nim, password = password)

            uiState = if (result !=null) {
                UiState(
                    isLoading = false,
                    isSuccess = true,
                    message = "Login successful"
                )
            } else {
                UiState(
                    isLoading = false,
                    isSuccess = false,
                    message = "Login failed"
                )
            }
        }
    }
    fun register(nim: String, email: String, password: String) {
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
                    message = "Email already used"
                )
            }
        }
    }
}
