package com.example.rentfage.ui.screen

import android.content.ContentResolver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.rentfage.R
import com.example.rentfage.data.local.Casa
import com.example.rentfage.ui.viewmodel.CasasViewModel

@Composable
fun FavoritosScreenVm(
    vm: CasasViewModel,
    onHouseClick: (Int) -> Unit
) {
    val state by vm.uiState.collectAsState()
    // Filtra la lista de casas para mostrar solo las marcadas como favoritas.
    val casasFavoritas = state.casas.filter { it.isFavorite }

    FavoritosScreen(
        casas = casasFavoritas,
        onHouseClick = onHouseClick,
        onToggleFavorite = { casaId -> vm.toggleFavorite(casaId) }
    )
}

@Composable
private fun FavoritosScreen(
    casas: List<Casa>,
    onHouseClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Mis Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Si la lista de favoritos está vacía, muestra un mensaje.
        if (casas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no has añadido ninguna casa a favoritos.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Si hay favoritos, los muestra en una lista.
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(casas) { casa ->
                    HouseCardFavorites(
                        casa = casa,
                        onClick = { onHouseClick(casa.id) },
                        onToggleFavorite = { onToggleFavorite(casa.id) }
                    )
                }
            }
        }
    }
}

// Componente que representa la tarjeta de una casa en la lista de favoritos.
@Composable
private fun HouseCardFavorites(
    casa: Casa,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                // Carga la imagen de la casa desde su URI usando Coil.
                AsyncImage(
                    model = casa.imageUri.toUri(),
                    contentDescription = "Imagen de la casa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (casa.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Marcar como favorito",
                        tint = if (casa.isFavorite) Color.Red else Color.White
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = casa.price, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = casa.address, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = casa.details, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

// Función auxiliar para construir la URI de un recurso drawable para las vistas previas.
private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true, name = "Favoritos con contenido")
@Composable
fun FavoritosScreenPreview() {
    val casasDeEjemplo = listOf(
        Casa(2, "UF 28.900", "Vitacura, sector Santa María de Manquehue", "5 hab | 4 baños | 620 m²", resourceUri(R.drawable.casa2), -33.3592, -70.5150, true),
        Casa(3, "UF 19.800", "Las Condes, sector El Golf", "3 hab | 3 baños | 340 m²", resourceUri(R.drawable.casa3), -33.3989, -70.5303, true)
    )
    FavoritosScreen(
        casas = casasDeEjemplo,
        onHouseClick = {},
        onToggleFavorite = {}
    )
}

@Preview(showBackground = true, name = "Favoritos sin contenido")
@Composable
fun FavoritosScreenEmptyPreview() {
    FavoritosScreen(
        casas = emptyList(),
        onHouseClick = {},
        onToggleFavorite = {}
    )
}
