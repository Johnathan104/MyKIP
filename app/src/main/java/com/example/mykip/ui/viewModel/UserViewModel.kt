package com.example.mykip.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.User
import com.example.mykip.repository.UserRepository
import com.example.mykip.viewmodel.RiwayatDanaViewModel
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
    fun loadUser(nim: String) {
        viewModelScope.launch {
            loggedInUser = repository.getUserByNim(nim)
        }
    }
    fun getByNim(nim: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getUserByNim(nim))
        }
    }
    fun getAllUsers(onResult: (List<User>) -> Unit){
        viewModelScope.launch {
            onResult(repository.getAllUsers())
        }
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
            // Auto admin untuk NIM tertentu
            if (nim == "412022011") {
                repository.setAdmin(nim, true)
            }

            uiState = UiState(isLoading = true)

            val result = repository.login(nim = nim, password = password)

            uiState = if (result != null) {
                loggedInUser = result
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

    fun penyetoran(
        nim: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel
    ) {
        viewModelScope.launch {
            val user = repository.getUserByNim(nim) ?: return@launch

            val updatedBalance = user.balance + jumlah
            val flupdatedBalance = updatedBalance.toFloat()
            repository.updateBalance(nim, updatedBalance)

            riwayatViewModel.insertRiwayat(
                nim = nim,
                jumlah = jumlah,
                masuk = true,
                keterangan = keterangan
            )

            loadUser(nim) // refresh state
        }
    }
    fun penarikan(
        nim: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel,
        onError: (String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = repository.getUserByNim(nim) ?: return@launch
            uiState = UiState(
                isLoading = true,
                isSuccess = false,
            )
            if (user.balance < jumlah) {
                uiState = UiState(
                    isLoading = false,
                    isSuccess = false,
                    message = "Dana tidak mencukupi untuk penarikan"
                )
                return@launch
            }

            val updatedBalance = user.balance - jumlah
            repository.updateBalance(nim, updatedBalance)

            riwayatViewModel.insertRiwayat(
                nim = nim,
                jumlah = jumlah,
                masuk = false,
                keterangan = keterangan
            )
            uiState = UiState(
                isLoading = false,
                isSuccess = true,
                message = "Berhasil melakukan penarikan"
            )
            loadUser(nim) // refresh UI
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
            if(result){
                loggedInUser = User(nim = nim, email = email, password = password)
            }
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
