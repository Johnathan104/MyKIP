package org.whynot.kipku.repository

import org.whynot.kipku.data.RiwayatDana
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    fun listenByNimRealtime(nim: String, onDataChanged: (List<RiwayatDana>) -> Unit): ListenerRegistration {
        return collection
            .whereEqualTo("nim", nim)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    onDataChanged(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot
                    .toObjects(RiwayatDana::class.java)
                    .sortedByDescending { it.tanggal } // newest first

                onDataChanged(list)
            }
    }

    suspend fun getByNim(nim: String): List<RiwayatDana> {
        return try {
            val snapshot = collection
                .whereEqualTo("nim", nim)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emptyList()
            } else {
                snapshot.toObjects(RiwayatDana::class.java)
                    .sortedBy { it.tanggal } // ascending
            }
        } catch (e: Exception) {
            // If Firestore throws missing index error or offline state
            emptyList()
        }
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
