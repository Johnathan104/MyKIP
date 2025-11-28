package com.example.mykip.repository

import com.example.mykip.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val usersRef = db.collection("users")

    // -----------------------------------------------------
    // REGISTER USER (create auth + firestore document)
    // -----------------------------------------------------
    fun register(
        nim: String,
        email: String,
        password: String,
        isMahasiswa: Boolean,
        callback: (User?, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid ?: return@addOnSuccessListener

                val user = User(
                    uid = uid,
                    nim = if (isMahasiswa) nim else "",
                    email = email,
                    password = password,
                    balance = 0,
                    isAdmin = false
                )

                usersRef.document(uid)
                    .set(user)
                    .addOnSuccessListener { callback(user, null) }
                    .addOnFailureListener { callback(null, it.message) }
            }
            .addOnFailureListener { callback(null, it.message) }
    }


    // -----------------------------------------------------
    // LOGIN (nim + password)
    // -----------------------------------------------------
    fun login(
        nim: String,
        password: String,
        callback: (User?, String?) -> Unit
    ) {
        usersRef
            .whereEqualTo("nim", nim)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    callback(null, "NIM atau password salah")
                    return@addOnSuccessListener
                }

                val user = snap.documents[0].toObject(User::class.java)!!
                val email = user.email

                // Login ke Firebase Auth pakai email
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { callback(user, null) }
                    .addOnFailureListener { callback(null, it.message) }
            }
            .addOnFailureListener { callback(null, it.message) }
    }

    // -----------------------------------------------------
    // GET USER BY NIM
    // -----------------------------------------------------
    fun getUserByNim(nim: String, callback: (User?) -> Unit) {
        usersRef.whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { snap ->
                callback(
                    if (!snap.isEmpty) snap.documents[0].toObject(User::class.java) else null
                )
            }
            .addOnFailureListener { callback(null) }
    }

    // -----------------------------------------------------
    // GET ALL USERS
    // -----------------------------------------------------
    fun getAllUsers(callback: (List<User>) -> Unit) {
        usersRef.get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(User::class.java) }
                callback(list)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    // -----------------------------------------------------
    // UPDATE USER
    // -----------------------------------------------------
    fun updateUser(user: User) {
        if (user.uid.isBlank()) return
        usersRef.document(user.uid).set(user)
    }

    // -----------------------------------------------------
    // DELETE USER
    // -----------------------------------------------------
    fun deleteUser(user: User) {
        if (user.uid.isBlank()) return
        usersRef.document(user.uid).delete()
    }

    // -----------------------------------------------------
    // SET ADMIN
    // -----------------------------------------------------
    fun setAdmin(nim: String, isAdmin: Boolean) {
        usersRef.whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { snap ->
                if (!snap.isEmpty) {
                    usersRef.document(snap.documents[0].id)
                        .update("isAdmin", isAdmin)
                }
            }
    }

    // -----------------------------------------------------
    // UPDATE BALANCE
    // -----------------------------------------------------
    fun updateBalance(nim: String, balance: Int) {
        usersRef.whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { snap ->
                if (!snap.isEmpty) {
                    usersRef.document(snap.documents[0].id)
                        .update("balance", balance)
                }
            }
    }
}