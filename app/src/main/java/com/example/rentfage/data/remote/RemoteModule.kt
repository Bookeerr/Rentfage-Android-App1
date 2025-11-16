package com.example.rentfage.data.remote

// Importamos las librerías necesarias para construir el cliente de red.
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que se encarga de construir y proveer el cliente de Retrofit.
 * Centraliza la configuración de la conexión a la API.
 */
object RemoteModule {

    // La URL base del microservicio o API a la que nos conectaremos.
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // Creamos un interceptor de logging que nos permitirá ver en el Logcat
    // los detalles de las peticiones y respuestas de red. Muy útil para depurar.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Construimos un cliente OkHttp personalizado, añadiendo el interceptor de logging.
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Construimos la instancia principal de Retrofit.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // 1. Le decimos cuál es la URL base.
        .client(okHttp)      // 2. Le asignamos nuestro cliente OkHttp con logging.
        .addConverterFactory(GsonConverterFactory.create()) // 3. Le decimos que use Gson para convertir JSON a objetos Kotlin.
        .build()

    /**
     * Función genérica que crea una implementación de una interfaz de API (como JsonPlaceholderApi).
     */
    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
