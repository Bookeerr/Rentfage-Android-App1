package com.example.rentfage.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.data.local.storage.UserPreferences
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

// Esta es la funcion que se llamara desde el NavGraph.
@Composable
fun HomeScreenVm(onHouseClick: (Int) -> Unit, casasViewModel: CasasViewModel) {
    val uiState by casasViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    val locationLogic = rememberLocationLogic(context)

    HomeScreen(
        casas = uiState.casas,
        isLoggedIn = isLoggedIn,
        onHouseClick = onHouseClick,
        onToggleFavorite = { casa -> casasViewModel.toggleFavorite(casa) },
        onRequestLocation = locationLogic.requestLocationPermission
    )
}

@Composable
private fun HomeScreen(
    casas: List<CasaEntity>,
    isLoggedIn: Boolean,
    onHouseClick: (Int) -> Unit,
    onToggleFavorite: (CasaEntity) -> Unit,
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
                onToggleFavorite = { onToggleFavorite(casa) }
            )
        }
    }
}

@Composable
private fun HouseCard(
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = casa.details, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun rememberLocationLogic(context: Context): LocationLogic {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.lastOrNull()?.let {
                Toast.makeText(context, "Ubicación encontrada!", Toast.LENGTH_SHORT).show()
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    val settingsResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startLocationUpdates()
        } else {
            Toast.makeText(context, "La ubicación debe estar activada para buscar.", Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            val settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
            val settingsClient = LocationServices.getSettingsClient(context)
            settingsClient.checkLocationSettings(settingsRequest)
                .addOnSuccessListener { startLocationUpdates() }
                .addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                            settingsResolutionLauncher.launch(intentSenderRequest)
                        } catch (_: IntentSender.SendIntentException) { }
                    }
                }
        } else {
            Toast.makeText(context, "Permiso denegado.", Toast.LENGTH_SHORT).show()
        }
    }
    return LocationLogic(requestLocationPermission = { requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) })
}

data class LocationLogic(val requestLocationPermission: () -> Unit)
