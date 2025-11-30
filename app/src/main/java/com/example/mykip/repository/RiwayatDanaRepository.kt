package com.example.mykip.repository

import com.example.mykip.data.RiwayatDana
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RiwayatDanaRepository(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("riwayat")

    suspend fun insert(riwayatDana: RiwayatDana) {
        collection.add(riwayatDana).await()
    }

    suspend fun get(): List<RiwayatDana> {
        val snapshot = collection
            .get()
            .await()

        // 🔥 Handle empty collection (return empty list safely)
        if (snapshot.isEmpty) return emptyList()

        return snapshot.toObjects(RiwayatDana::class.java).sortedBy{it.tanggal}
    }

    suspend fun getByNim(nim: String): List<RiwayatDana> {
        val snapshot = collection
            .whereEqualTo("nim", nim)
            .get()
            .await()

        // 🔥 Handle "no riwayat for this NIM"
        if (snapshot.isEmpty) return emptyList()

        return snapshot.toObjects(RiwayatDana::class.java).sortedBy{it.tanggal}
    }

    suspend fun delete(riwayatDana: RiwayatDana) {
        val querySnapshot = collection
            .whereEqualTo("nim", riwayatDana.nim)
            .whereEqualTo("tanggal", riwayatDana.tanggal)
            .whereEqualTo("jumlah", riwayatDana.jumlah)
            .whereEqualTo("keterangan", riwayatDana.keterangan)
            .limit(1)
            .get()
            .await()

        // 🔥 If database empty OR no match → do nothing
        if (querySnapshot.isEmpty) return

        val documentId = querySnapshot.documents.first().id
        collection.document(documentId).delete().await()
    }
}
