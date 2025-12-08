package com.example.mykip.repository

import com.example.mykip.data.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MahasiswaRepository(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("mahasiswa")

    suspend fun insert(mahasiswa: Mahasiswa) {
        collection.document(mahasiswa.nim).set(mahasiswa).await()
    }

    suspend fun getAll(): List<Mahasiswa> {
        return collection.get().await().toObjects(Mahasiswa::class.java)
    }

    suspend fun getByNim(nim: String): Mahasiswa? {
        val doc = collection.document(nim).get().await()
        return if (doc.exists()) doc.toObject(Mahasiswa::class.java) else null
    }

    suspend fun update(mahasiswa: Mahasiswa) {
        // 1. Check duplicate emailWali
//        if (mahasiswa.emailWali!!.isNotBlank()) {
//            val existing = collection
//                .whereEqualTo("emailWali", mahasiswa.emailWali)
//                .get()
//                .await()
//
//            val duplicateExists = existing.documents.any { doc ->
//                doc.id != mahasiswa.nim   // different mahasiswa, same email
//            }
//
//            if (duplicateExists) {
//                throw IllegalStateException("Email wali sudah digunakan mahasiswa lain.")
//            }
//        }

        // 2. If no duplicate → update normally
        collection.document(mahasiswa.nim).set(mahasiswa).await()
    }


    suspend fun delete(mahasiswa: Mahasiswa) {
        collection.document(mahasiswa.nim).delete().await()
    }
}
