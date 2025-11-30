package com.example.mykip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_users")

data class User(
    @PrimaryKey
    val uid: String = "",        // default value so Firestore can create User()
    val nim: String = "",
    val email: String = "",
    val password: String = "",
    val balance: Int = 0,
    val role:String = "",
)
