package com.example.mykip.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mykip.data.Mahasiswa
import com.example.mykip.data.RiwayatDana
import com.example.mykip.data.User
import com.example.mykip.repository.RiwayatDanaRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatDanaViewModel(
    private val repository: RiwayatDanaRepository
) : ViewModel() {
    private val firestore = Firebase.firestore
    private val collection = firestore.collection("riwayat")
    fun getTodayDate(): Timestamp {
        return Timestamp(Date()) // Create a new Timestamp from the current Date
    }
     fun statusRiwayatGanti(
        riwayatDanaId: String,
        status: String,
        userRole: String = "mahasiswa"
    ) {
         viewModelScope.launch{
             // Ambil data riwayat dari Firestore
             val riwayatSnapshot = collection
                 .document(riwayatDanaId)
                 .get()
                 .await()

             val riwayat = riwayatSnapshot.toObject(RiwayatDana::class.java)
                 ?: return@launch
             val prevstatus = riwayat.status
             val nimFromRiwayat = riwayat.nim      // <-- Nim diambil dari riwayatDana
             val amount = riwayat.jumlah
             val goingIn = riwayat.goingIn
             // Update status riwayat
             collection.document(riwayatDanaId)
                 .update("status", status)
                 .addOnSuccessListener {
                     Log.d("RiwayatDanaRepo", "Status updated to $status for $riwayatDanaId")
                 }
                 .addOnFailureListener { e ->
                     Log.e("RiwayatDanaRepo", "Failed to update status", e)
                 }

                   // true = saldo masuk, false = saldo keluar

             // Jika bukan admin → tidak boleh update saldo
             if (userRole != "admin") return@launch

             // Cari user berdasarkan nim dari riwayat
             val userSnapshot = firestore.collection("users")
                 .whereEqualTo("nim", nimFromRiwayat)
                 .whereEqualTo("role", "mahasiswa")
                 .get()
                 .await()

             val userDoc = userSnapshot.documents.firstOrNull() ?: return@launch
             val user = userDoc.toObject(User::class.java) ?: return@launch

             val userDocId = userDoc.id



             // Hitung saldo baru berdasarkan goingIn
             var newBalance = user.balance
             if(status == "approved" && prevstatus != "approved"){
                 if (goingIn == false) {
                     newBalance -= amount
                 }
             }
             if(status == "rejected" && prevstatus == "approved"){
                 if(goingIn == false){
                     newBalance += amount
                 }else{
                     newBalance -= amount
                 }
             }

             // Update balance user
             firestore.collection("users")
                 .document(userDocId)
                 .update("balance", newBalance)
                 .await()

             Log.d("RiwayatDanaRepo", "status $status previous status $prevstatus , Balance updated to $newBalance for nim=$nimFromRiwayat")
         }
    }

    fun tambahRiwayat(
        nim: String,
        jumlah: Int,
        keterangan: String,
        jenis: String,
        goingIn:Boolean =false,
        buktiTransfer: String?,            // 🔥 tambahkan ini
        userRole: String = "mahasiswa"
    ) {
        val status = if (userRole != "admin") "pending" else "approved"

        val data = hashMapOf(
            "nim" to nim,
            "goingIn" to goingIn,
            "jumlah" to jumlah,
            "keterangan" to keterangan,
            "jenis" to jenis,
            "status" to status,
            "timestamp" to System.currentTimeMillis(),
            "bukti_transfer" to buktiTransfer  // 🔥 field baru
        )


        FirebaseFirestore.getInstance()
            .collection("riwayat")
            .add(data)
    }

    private val _riwayatList = MutableStateFlow<List<RiwayatDana>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatDana>> = _riwayatList

    fun getAll() {
        viewModelScope.launch {
            _riwayatList.value = repository.get()
        }
    }

    fun getByNim(nim: String, onResult: (List<RiwayatDana>) -> Unit) {
        collection
            .whereEqualTo("nim", nim)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot.documents.map { doc ->
                    var item = doc.toObject(RiwayatDana::class.java)
                    item?.id = doc.id   // 🔥 attach the actual Firestore document ID
                    item!!
                }.sortedBy { it.tanggal }

                onResult(list)
            }
    }


    fun insertRiwayat(nim: String, jumlah: Int, masuk: Boolean, keterangan: String) {
        viewModelScope.launch {
            repository.insert(
                RiwayatDana(
                    nim = nim,
                    tanggal = getTodayDate(),
                    goingIn = masuk,
                    jumlah = jumlah,
                    keterangan = keterangan
                )
            )
            getAll()
        }
    }

    private var listener: ListenerRegistration? = null

    fun listenRiwayatByNim(nim: String, onUpdate: (List<RiwayatDana>) -> Unit) {
        listener?.remove() // remove old listener if exists

        listener = repository.listenByNimRealtime(nim) { list ->
            onUpdate(list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }


    fun delete(riwayatDana: RiwayatDana) {
        viewModelScope.launch {
            repository.delete(riwayatDana)
            getAll()
        }
    }
}
