package com.example.rentfage.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,                                      // Nombre
    val email: String,                                     // Email
    val phone: String,                                     // Teléfono
    val pass: String
)
