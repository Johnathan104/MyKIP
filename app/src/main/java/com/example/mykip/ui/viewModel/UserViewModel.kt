package com.example.mykip.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.User
import com.example.mykip.viewmodel.RiwayatDanaViewModel
import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.SessionManager
import com.example.mykip.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(
    private val repository: UserRepository,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager// KEEPED for compatibility (even unused)
) : ViewModel() {
    fun getMahasiswaByWali(waliEmail: String, onResult: (List<User>) -> Unit) {
        db.collection("users")
            .whereEqualTo("role", "mahasiswa")
            .whereEqualTo("emailWali", waliEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    Log.i("anakList", "Error fetching anak list: $error")
                    return@addSnapshotListener
                }
                Log.i("anakList", "being snapshotted: $waliEmail")
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.apply {
                        uid = doc.id      // if you have ID field, fill it here
                    }
                }

                onResult(list)
            }
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users")
    var userAnak:User? by mutableStateOf(null)

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
    fun loadUserFromSession() {
        viewModelScope.launch {
            // Get the saved email from SessionManager. first() gets the current value.
            val userEmail = sessionManager.userEmailFlow.first()
            if (userEmail != null) {
                // If an email is found in the session, fetch the full user object
                val snap = db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .await()

                // Set the loggedInUser with the data from Firestore
                loggedInUser = snap.documents.firstOrNull()?.toObject(User::class.java)
            }
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
                sessionManager.saveLoginSession(user.email)
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
            val isAdmin = admin?.role == "admin"
            if (isAdmin != true) return@launch

            // Query user dengan NIM + role = mahasiswa
            val snap = db.collection("users")
                .whereEqualTo("nim", nim)
                .whereEqualTo("role", "mahasiswa") // <-- filter role
                .get()
                .await()

            val target = snap.documents.firstOrNull() ?: return@launch
            val user = target.toObject(User::class.java) ?: return@launch

            val newBalance = user.balance + jumlah

            db.collection("users")
                .document(target.id)
                .update("balance", newBalance)
                .await()
            riwayatViewModel.tambahRiwayat(
                nim,
                jumlah,
                keterangan,
                "Transfer kepada Mahasiswa",
                true,
                loggedInUser!!.role
            )

            if (loggedInUser?.nim == nim) loadUserFromSession()
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
                .whereEqualTo("role", "mahasiswa")   // <-- NEW CONDITION
                .get()
                .await()

            val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
                ?: return@launch

            uiState = UiState(true, false)

            if (user.balance < jumlah) {
                uiState = UiState(
                    isLoading = false,
                    isSuccess = false,
                    message = "Dana tidak mencukupi untuk penarikan"
                )
                return@launch
            }

            val newBalance = user.balance - jumlah

// Update the matched document
            val userDocId = snapshot.documents.first().id

            db.collection("users")
                .document(userDocId)
                .update("balance", newBalance)
                .await()

            riwayatViewModel.tambahRiwayat(nim, jumlah, keterangan, "Transfer oleh Mahasiswa", false)

            val successMsg = "Transfer sejumlah Rp.$jumlah berhasil."
            Log.i("TransferDebug", "Success: $successMsg")
            uiState = UiState(isSuccess = true, message = successMsg)

            uiState = UiState(false, true, "Berhasil melakukan penarikan")

            loadUserFromSession()
            loadUserFromSession()
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
            if (isMahasiswa){
                val snap = db.collection("users")
                    .whereEqualTo("nim", nim)
                    .whereEqualTo("role", "mahasiswa")
                    .get()
                    .await()
                if (!snap.isEmpty()) {
                    uiState = UiState(false, false, "NIM sudah terdaftar")
                    return@launch}
            }
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

                uiState = UiState(false, true, "Registration successful! Proceed to login")
            } catch (e: Exception) {
                uiState = UiState(false, false, "Registration failed: ${e.message}")
            }
        }
    }
    fun getAnakUser(user:User){
        viewModelScope.launch{
            val snap = db.collection("users")
                .whereEqualTo("nim", user.nim)
                .whereEqualTo("role", "mahasiswa")
                .get()
                .await()
            userAnak = snap.documents.firstOrNull()?.toObject(User::class.java)

        }
    }
     fun update(user: User) {
         viewModelScope.launch{
             val snap = db.collection("users")
                 .whereEqualTo("email", user.email)
                 .get()
                 .await()
             db.collection("users")
                 .document(snap.documents.first().id)
                 .set(user)
                 .await()

         }
    }




    // LOGOUT
    fun logout() {
        viewModelScope.launch {
            // 🔥 CLEAR THE SESSION
            sessionManager.clearLoginSession()
            auth.signOut()
            loggedInUser = null
            // Reset any other relevant state
            resetState()
        }
    }
}
