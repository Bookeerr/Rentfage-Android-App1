package com.example.rentfage.data.remote

// Importamos el DTO y las anotaciones de Retrofit
import com.example.rentfage.data.remote.dto.PostDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Interfaz que define los endpoints de la API para que Retrofit los implemente.
interface JsonPlaceholderApi {

    // Obtiene una lista de todos los posts.
    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    // Obtiene un post especifico por su ID.
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): PostDto

    // Crea un nuevo post enviando un objeto PostDto en el cuerpo de la peticion.
    @POST("posts")
    suspend fun createPost(@Body post: PostDto): PostDto

    // Actualiza un post existente identificado por su ID.
    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Int,
        @Body post: PostDto
    ): PostDto

    // Elimina un post identificado por su ID.
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>
}
