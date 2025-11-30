package com.example.mykip.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.User
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.example.mykip.data.RiwayatDana
import com.example.mykip.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(
    private val repository: UserRepository // KEEPED for compatibility (even unused)
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState by mutableStateOf(UiState())
        private set

    var loggedInUser: User? by mutableStateOf(null)
        private set

    // RESET
    fun resetState() {
        uiState = UiState()
    }

    // LOAD USER (from Firestore)
    fun loadUser(nim: String) {
        viewModelScope.launch {
            val snapshot = db.collection("users")
                .whereEqualTo("nim", nim)
                .get()
                .await()

            loggedInUser = snapshot.documents.firstOrNull()?.toObject(User::class.java)
        }
    }

    // GET USER BY NIM
    fun getByNim(nim: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val snapshot = db.collection("users")
                .whereEqualTo("nim", nim)
                .get()
                .await()

            onResult(snapshot.documents.firstOrNull()?.toObject(User::class.java))
        }
    }

    // GET ALL USERS
    fun getAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch {
            val list = db.collection("users").get().await()
                .documents.mapNotNull { it.toObject(User::class.java) }

            onResult(list)
        }
    }

    // UPDATE USER
    fun updateUser(user: User) {
        viewModelScope.launch {
            db.collection("users")
                .document(user.uid)
                .set(user)
                .await()
        }
    }

    // DELETE USER
    fun deleteUser(user: User) {
        viewModelScope.launch {
            db.collection("users")
                .document(user.uid)
                .delete()
                .await()
        }
    }

    // LOGIN (nim + password → convert to email internally)
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = UiState(error = "Email and Password cannot be empty")
            return
        }

        viewModelScope.launch {
            uiState = UiState(isLoading = true)

            val snap = db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            val user = snap.documents.firstOrNull()?.toObject(User::class.java)

            if (user == null) {
                uiState = UiState(error = "Login failed: NIM atau password salah")
                return@launch
            }

            try {
                auth.signInWithEmailAndPassword(user.email, password).await()
                loggedInUser = user
                uiState = UiState(isSuccess = true)
            } catch (e: Exception) {
                uiState = UiState(error = "Auth failed: ${e.message}")
            }
        }
    }


    // SETOR / PENYETORAN
    fun penyetoran(
        nim: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel
    ) {
        viewModelScope.launch {
            val admin = loggedInUser
            val isAdmin = admin?.role =="admin"
            if (isAdmin != true) return@launch

            val snap = db.collection("users")
                .whereEqualTo("nim", nim)
                .get()
                .await()

            val target = snap.documents.firstOrNull() ?: return@launch
            val user = target.toObject(User::class.java) ?: return@launch

            val newBalance = user.balance + jumlah

            db.collection("users")
                .document(target.id)
                .update("balance", newBalance)
                .await()

            riwayatViewModel.insertRiwayat(nim, jumlah, true, keterangan)

            if (loggedInUser?.nim == nim) loadUser(nim)
        }
    }

    // PENARIKAN
    fun penarikan(
        nim: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel,
        onError: (String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            val snapshot = db.collection("users")
                .whereEqualTo("nim", nim)
                .get()
                .await()

            val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
                ?: return@launch

            uiState = UiState(true, false)

            if (user.balance < jumlah) {
                uiState = UiState(false, false, "Dana tidak mencukupi untuk penarikan")
                return@launch
            }

            val newBalance = user.balance - jumlah

            db.collection("users")
                .document(snapshot.documents.first().id)
                .update("balance", newBalance)
                .await()

            riwayatViewModel.insertRiwayat(nim, jumlah, false, keterangan)

            uiState = UiState(false, true, "Berhasil melakukan penarikan")

            loadUser(nim)
        }
    }

    // REGISTRATION → firebase auth + firestore
    // REGISTRATION → firebase auth + firestore
    fun register(
        nim: String,
        nama: String,
        email: String,
        password: String,
        role: String,
        jurusan: String? = null,
        jenjang: String? = null,
        kuliah: String? = null
    )
    {
        val isMahasiswa = role == "mahasiswa"

        // FORCE ADMIN UNTUK ISAIAH
        val finalRole = if (email == "isaiah@gmail.com" && nim == "412022011") {
            "admin"
        } else {
            role
        }

        if (finalRole == "mahasiswa" && (nim.isBlank() || email.isBlank() || password.isBlank())) {
            uiState = UiState(false, false, "All fields are required (Mahasiswa)")
            return
        }

        if (finalRole != "mahasiswa" && (email.isBlank() || password.isBlank())) {
            uiState = UiState(false, false, "Email & Password required (Orang Tua)")
            return
        }

        viewModelScope.launch {
            uiState = UiState(isLoading = true)

            try {
                val authResult =
                    auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user!!.uid

                val newUser = User(
                    uid = uid,
                    nim = nim,
                    nama = nama,
                    email = email,
                    password = "-",
                    balance = 0,
                    role = finalRole  // <- ROLE SUDAH FIX DARI FINALROLE
                )

                db.collection("users").document(uid).set(newUser).await()

                loggedInUser = newUser

                uiState = UiState(false, true, "Registration successful")
            } catch (e: Exception) {
                uiState = UiState(false, false, "Registration failed: ${e.message}")
            }
        }
    }



    // LOGOUT
    fun logout() {
        auth.signOut()
        loggedInUser = null
        resetState()
    }
}
