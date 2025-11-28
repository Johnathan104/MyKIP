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
        collection.document(mahasiswa.nim).set(mahasiswa).await()
    }

    suspend fun delete(mahasiswa: Mahasiswa) {
        collection.document(mahasiswa.nim).delete().await()
    }
}
