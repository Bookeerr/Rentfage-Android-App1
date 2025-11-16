package com.example.rentfage.data.remote.dto

data class PostDto(
    val userId: Int,   // El ID del autor del post
    val id: Int,       // El ID único del post
    val title: String, // El título del post
    val body: String   // El contenido o cuerpo del post
)
