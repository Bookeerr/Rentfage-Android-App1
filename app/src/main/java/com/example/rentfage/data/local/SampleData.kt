package com.example.rentfage.data.local

import android.content.ContentResolver
import com.example.rentfage.R

// El orden de los parámetros ha sido corregido.
data class Casa(
    val id: Int,
    val price: String,
    val address: String,
    val details: String,
    var imageUri: String, // Se mantiene como String para la URI.
    val latitude: Double,
    val longitude: Double,
    var isFavorite: Boolean = false
)

private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

// La lista de ejemplo ahora usa el orden de parámetros correcto.
val casasDeEjemplo: List<Casa> = listOf(
    Casa(1, "UF 28.500", "Av. Vitacura, Vitacura, Santiago", "4 hab | 1 baño | 450 m²", resourceUri(R.drawable.casa1), -33.4130, -70.5947),
    Casa(2, "UF 35.000", "Camino La Dehesa, Lo Barnechea, Santiago", " 4 hab | 1 baño | 600 m² | Piscina", resourceUri(R.drawable.casa2), -33.3592, -70.5150),
    Casa(3, "UF 26.500", "San Damián, Las Condes, Santiago", "4 hab | 1 baño | 500 m² | Jardín amplio", resourceUri(R.drawable.casa3), -33.3989, -70.5303),
    Casa(4, "UF 18.000", "Isidora Goyenechea, Las Condes, Santiago", "3 hab | 1 baño | 220 m² | Penthouse", resourceUri(R.drawable.casa4), -33.4100, -70.5986)
)
