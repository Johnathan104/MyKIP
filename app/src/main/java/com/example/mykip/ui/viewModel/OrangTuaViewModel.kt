package com.example.mykip.viewmodel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mykip.data.OrangTua
import com.example.mykip.data.*
import com.example.mykip.repository.OrangTuaRepository
import com.example.mykip.ui.viewModel.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrangTuaViewModel(
    private val repository: OrangTuaRepository
) : ViewModel() {
    fun transferKeAnak(
        nimAnak: String,
        jumlah: Int,
        keterangan: String,
        riwayatViewModel: RiwayatDanaViewModel
    ) {
        val db = FirebaseFirestore.getInstance()

        // 1. Kurangi saldo orang tua
        db.collection("users")
            .whereEqualTo("email", loggedInOrtu?.email)
            .get()
            .addOnSuccessListener { snap ->
                val parentDoc = snap.documents.firstOrNull() ?: return@addOnSuccessListener
                val parent = parentDoc.toObject(User::class.java) ?: return@addOnSuccessListener

                val newParentBalance = parent.balance - jumlah
                db.collection("users")
                    .document(parentDoc.id)
                    .update("balance", newParentBalance)

                // 2. Tambah saldo anak
                db.collection("users")
                    .whereEqualTo("nim", nimAnak)
                    .get()
                    .addOnSuccessListener { childSnap ->
                        val childDoc = childSnap.documents.firstOrNull() ?: return@addOnSuccessListener
                        val child = childDoc.toObject(User::class.java) ?: return@addOnSuccessListener

                        val newChildBalance = child.balance + jumlah
                        db.collection("users")
                            .document(childDoc.id)
                            .update("balance", newChildBalance)

                        // 3. Tambah riwayat
                        riwayatViewModel.insertRiwayat(
                            nim = nimAnak,
                            jumlah = jumlah,
                            masuk = true,
                            keterangan = keterangan
                        )
                    }
            }
    }


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
// GET ALL CHILDREN BY PARENT ID
// ---------------------------------------------------------
    fun getAnakByParent(
        parentId: String,
        callback: (List<Mahasiswa>) -> Unit
    ) {
        db.collection("orangTua")
            .document(parentId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                // Ambil list array anak NIM
                val anakNimList = doc.get("anakNim") as? List<String> ?: emptyList()

                if (anakNimList.isEmpty()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                // Query mahasiswa berdasarkan daftar NIM
                db.collection("mahasiswa")
                    .whereIn("nim", anakNimList)
                    .get()
                    .addOnSuccessListener { snap ->

                        val anakList = snap.documents.mapNotNull { it.toObject(Mahasiswa::class.java) }

                        callback(anakList)
                    }
                    .addOnFailureListener {
                        callback(emptyList())
                    }
            }
            .addOnFailureListener {
                callback(emptyList())
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

