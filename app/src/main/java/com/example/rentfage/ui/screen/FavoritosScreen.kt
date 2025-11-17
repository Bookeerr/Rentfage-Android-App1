package com.example.rentfage.ui.screen

import android.content.ContentResolver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rentfage.R
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.ui.viewmodel.CasasViewModel

@Composable
fun FavoritosScreenVm(onHouseClick: (Int) -> Unit, casasViewModel: CasasViewModel) {
    val state by casasViewModel.favoritasUiState.collectAsStateWithLifecycle()

    FavoritosScreen(
        casas = state.casas,
        onHouseClick = onHouseClick,
        onToggleFavorite = { casa -> casasViewModel.toggleFavorite(casa) }
    )
}

@Composable
private fun FavoritosScreen(
    casas: List<CasaEntity>,
    onHouseClick: (Int) -> Unit,
    onToggleFavorite: (CasaEntity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Mis Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (casas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aun no has añadido ninguna casa a favoritos.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(casas) { casa ->
                    HouseCardFavorites(
                        casa = casa,
                        onClick = { onHouseClick(casa.id) },
                        onToggleFavorite = { onToggleFavorite(casa) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HouseCardFavorites(
    casa: CasaEntity,
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
            }
        }
    }
}

private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true, name = "Favoritos con contenido")
@Composable
fun FavoritosScreenPreview() {
    val casasDeEjemplo = listOf(
        CasaEntity(2, "UF 28.900", "Vitacura, sector Santa María de Manquehue", "5 hab | 4 baños | 620 m²", resourceUri(R.drawable.casa2), -33.3592, -70.5150, true),
        CasaEntity(3, "UF 19.800", "Las Condes, sector El Golf", "3 hab | 3 baños | 340 m²", resourceUri(R.drawable.casa3), -33.3989, -70.5303, true)
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
