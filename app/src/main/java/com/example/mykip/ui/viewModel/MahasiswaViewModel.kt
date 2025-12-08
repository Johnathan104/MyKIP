package com.example.mykip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.repository.MahasiswaRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mykip.data.User
import com.example.mykip.ui.viewModel.UiState
import com.google.firebase.firestore.FirebaseFirestore



class MahasiswaViewModel(
    private val repository: MahasiswaRepository
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set
    var mahasiswa:Mahasiswa? = null
    // 🔄 Reset uiState
    private fun resetUiState() {
        uiState = UiState()
    }
    fun insert(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            repository.insert(mahasiswa)
        }
    }
    fun delete(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            repository.delete(mahasiswa)
        }
    }
    fun getAll(onResult: (List<Mahasiswa>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getAll())
        }
    }

    // 📌 Fetch mahasiswa by NIM
    fun getByNim(nim: String, onResult: (Mahasiswa?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getByNim(nim))
        }
    }
    fun getMahasiswaByNim(nim: String, onResult: (Mahasiswa?) -> Unit) {
        getByNim(nim) { mahasiswa ->
            this.mahasiswa = mahasiswa
            onResult(mahasiswa)
        }
    }
    fun transferMahasiswa(
        nim: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { snap ->
                val doc = snap.documents.firstOrNull() ?: return@addOnSuccessListener
                val user = doc.toObject(User::class.java) ?: return@addOnSuccessListener

                // Kurangi saldo
                val newBalance = user.balance - jumlah
                db.collection("users")
                    .document(doc.id)
                    .update("balance", newBalance)

                // Simpan riwayat
                riwayatViewModel.insertRiwayat(
                    nim = nim,
                    jumlah = jumlah,
                    masuk = false,
                    keterangan = keterangan
                )
            }
    }
    // 📌 Update mahasiswa (with UiState)
    fun update(mahasiswa: Mahasiswa) {
        uiState = UiState(isLoading = true)

        viewModelScope.launch {
            try {
                repository.update(mahasiswa)
                uiState = UiState(
                    isLoading = false,
                    isSuccess = true,
                    message = "Email telah diperbarui."
                )
            } catch (e: Exception) {
                uiState = UiState(
                    isLoading = false,
                    isSuccess = false,
                    error = e.message ?: "Terjadi kesalahan saat update."
                )
            }
        }
    }
}
