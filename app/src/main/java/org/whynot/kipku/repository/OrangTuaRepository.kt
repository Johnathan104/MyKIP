package org.whynot.kipku.repository

import org.whynot.kipku.data.OrangTua
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrangTuaRepository() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val ortuRef = db.collection("orangTua")
    fun login(
        email: String,
        password: String,
        callback: (OrangTua?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                // After FirebaseAuth login success → Fetch orang tua data
                ortuRef
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { snapshot ->

                        if (!snapshot.isEmpty) {
                            val doc = snapshot.documents[0]
                            val orangTua = doc.toObject(OrangTua::class.java)
                            callback(orangTua, null)
                        } else {
                            callback(null, "Data orang tua tidak ditemukan")
                        }
                    }
                    .addOnFailureListener { callback(null, it.message) }
            }
            .addOnFailureListener { callback(null, it.message) }
    }

    fun insert(
        email: String,
        password: String,
        nama: String,
        anakNim: String,
        callback: (OrangTua?, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid ?: return@addOnSuccessListener

                val ortu = OrangTua(
                    email = email,
                    nama = nama,
                    anakNim = anakNim
                )

                ortuRef.document(uid)
                    .set(ortu)
                    .addOnSuccessListener { callback(ortu, null) }
                    .addOnFailureListener { callback(null, it.message) }
            }
            .addOnFailureListener { callback(null, it.message) }
    }
}
