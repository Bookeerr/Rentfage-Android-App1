package com.example.rentfage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "casas")
data class CasaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // El ID se autogenerará, por eso se inicializa a 0.
    val price: String,
    val address: String,
    val details: String,
    val imageUri: String,
    val latitude: Double,
    val longitude: Double,
    var isFavorite: Boolean = false
)
