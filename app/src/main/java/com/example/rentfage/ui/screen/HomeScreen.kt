package com.example.rentfage.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.IntentSender
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rentfage.R
import com.example.rentfage.data.local.Casa
import com.example.rentfage.data.local.storage.UserPreferences
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
@Composable
fun HomeScreenVm(
    vm: CasasViewModel,
    onHouseClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val userPrefrs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefrs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.lastOrNull()?.let { 
                Toast.makeText(context, "¡Ubicación encontrada!", Toast.LENGTH_SHORT).show()
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    val settingsResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                startLocationUpdates()
            } else {
                Toast.makeText(context, "La ubicación debe estar activada para buscar.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun checkSettingsAndStartLocationUpdates() {
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(context)

        settingsClient.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { startLocationUpdates() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        settingsResolutionLauncher.launch(intentSenderRequest)
                    } catch (_: IntentSender.SendIntentException) { 
                        Toast.makeText(context, "No se puede abrir el diálogo de ubicación", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se pueden verificar los ajustes de ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                checkSettingsAndStartLocationUpdates()
            } else {
                Toast.makeText(context, "Permiso denegado. No se puede buscar por ubicación.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val state by vm.uiState.collectAsState()

    HomeScreen(
        casas = state.casas,
        isLoggedIn = isLoggedIn,
        onHouseClick = onHouseClick,
        onToggleFavorite = { casaId -> vm.toggleFavorite(casaId) },
        onRequestLocation = {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    )
}

@Composable
private fun HomeScreen(
    casas: List<Casa>,
    isLoggedIn: Boolean,
    onHouseClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onRequestLocation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Propiedades Disponibles",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f)
                )
                Icon(
                    imageVector = if (isLoggedIn) Icons.Default.Person else Icons.Default.PersonOff,
                    contentDescription = if (isLoggedIn) "Usuario Logueado" else "Usuario no Logueado",
                    tint = if (isLoggedIn) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequestLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar Cerca de Mí")
            }
        }
        items(casas) { casa ->
            HouseCard(
                casa = casa,
                onClick = { onHouseClick(casa.id) },
                onToggleFavorite = { onToggleFavorite(casa.id) }
            )
        }
    }
}

@Composable
private fun HouseCard(
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
                // Se usa AsyncImage para cargar la imagen desde la URI.
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

private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val casasDeEjemplo = listOf(
        Casa(1, "UF 28.500", "Av. Vitacura, Vitacura, Santiago", "4 hab | 1 baño | 450 m²", resourceUri(R.drawable.casa1), -33.4130, -70.5947),
        Casa(2, "UF 35.000", "Camino La Dehesa, Lo Barnechea, Santiago", " 4 hab | 1 baño | 600 m² | Piscina", resourceUri(R.drawable.casa2), -33.3592, -70.5150)
    )
    HomeScreen(
        casas = casasDeEjemplo,
        isLoggedIn = true,
        onHouseClick = {},
        onToggleFavorite = {},
        onRequestLocation = {}
    )
}
