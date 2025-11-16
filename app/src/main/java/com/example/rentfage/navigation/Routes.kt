package com.example.rentfage.navigation

// Clase sellada para rutas
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    data object Home     : Route("home")     // Ruta Home
    data object Login    : Route("login")    // Ruta Login
    data object Register : Route("register") // Ruta Registro
    data object Perfil   : Route("perfil")   // ruta para el perfil
    data object Favoritos : Route("favoritos") // ruta para la pantalla de favoritos
    data object DetalleCasa : Route("detalle_casa/{casaId}") // ruta para el detalle de las casas
}

/*
* “Strings mágicos” se refiere a cuando pones un texto duro y repetido en varias partes del código,
* Si mañana cambias "home" por "inicio", tendrías que buscar todas las ocurrencias de "home" a mano.
* Eso es frágil y propenso a errores.
La idea es: mejor centralizar esos strings en una sola clase (Route), y usarlos desde ahí.*/