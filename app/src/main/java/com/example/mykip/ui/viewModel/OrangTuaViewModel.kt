package com.example.mykip.viewmodel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mykip.data.OrangTua
import com.example.mykip.repository.OrangTuaRepository
import com.example.mykip.ui.viewModel.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrangTuaViewModel(
    private val repository: OrangTuaRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState by mutableStateOf(UiState())
        private set

    var state by mutableStateOf(UiState())
        private set

    var loggedInOrtu: OrangTua? by mutableStateOf(null)
        private set


    // ---------------------------------------------------------
    // RESET STATES
    // ---------------------------------------------------------
    fun resetState() {
        uiState = UiState()
        state = UiState()
    }


    // ---------------------------------------------------------
    // LOGIN (UNCHANGED)
    // ---------------------------------------------------------
    fun login(email: String, password: String) {
        uiState = uiState.copy(isLoading = true, message = "")

        repository.login(email, password) { orangTua, error ->

            if (error != null) {
                uiState = uiState.copy(
                    isLoading = false,
                    message = error,
                    isSuccess = false
                )
                return@login
            }

            loggedInOrtu = orangTua

            uiState = uiState.copy(
                isLoading = false,
                message = "",
                isSuccess = true
            )
        }
    }


    // ---------------------------------------------------------
    // REGISTER (UNCHANGED)
    // ---------------------------------------------------------
    fun insert(email: String, password: String, nama: String, anakNim: String) {
        state = state.copy(isLoading = true, message = "")

        repository.insert(email, password, nama, anakNim) { ortu, error ->
            if (ortu != null) {

                loggedInOrtu = ortu

                state = UiState(
                    isLoading = false,
                    message = "Register berhasil"
                )
            } else {
                state = UiState(
                    isLoading = false,
                    message = error ?: "Unknown error"
                )
            }
        }
    }


    // ---------------------------------------------------------
    // LOAD ORANG TUA (DIRECTLY FROM FIRESTORE)
    // ---------------------------------------------------------
    fun loadOrtu(email: String) {
        uiState = uiState.copy(isLoading = true)

        db.collection("orangTua")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snap ->

                val ortu = snap.documents.firstOrNull()?.toObject(OrangTua::class.java)
                loggedInOrtu = ortu

                uiState = uiState.copy(
                    isLoading = false,
                    message = if (ortu == null) "Data tidak ditemukan" else "",
                    isSuccess = ortu != null
                )
            }
            .addOnFailureListener {
                uiState = uiState.copy(
                    isLoading = false,
                    message = "error terjadi",
                    isSuccess = false
                )
            }
    }


    // ---------------------------------------------------------
    // LOGOUT
    // ---------------------------------------------------------
    fun logout() {
        auth.signOut()
        loggedInOrtu = null
        resetState()
    }
}

