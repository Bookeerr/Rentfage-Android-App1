package com.example.rentfage.ui.screen

import android.content.ContentResolver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentfage.R
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.ui.viewmodel.EstadoSolicitud
import com.example.rentfage.ui.viewmodel.HistorialViewModel
import com.example.rentfage.ui.viewmodel.Solicitud

@Composable
fun AdminSolicitudesScreen(historialViewModel: HistorialViewModel) {
    LaunchedEffect(Unit) {
        historialViewModel.cargarTodasLasSolicitudes()
    }

    val state by historialViewModel.uiState.collectAsState()
//cambio
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Gestionar Solicitudes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.solicitudes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay ninguna solicitud pendiente.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.solicitudes) { solicitud ->
                    AdminSolicitudCard(
                        solicitud = solicitud,
                        onAprobar = { historialViewModel.aprobarSolicitud(solicitud.id) },
                        onRechazar = { historialViewModel.rechazarSolicitud(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminSolicitudCard(
    solicitud: Solicitud,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Solicitud #${solicitud.id}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Usuario: ${solicitud.usuarioEmail}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Propiedad: ${solicitud.casa.address}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha: ${solicitud.fecha}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Estado: ${solicitud.estado.name}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            
            if (solicitud.estado == EstadoSolicitud.Pendiente) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onRechazar) { Text("Rechazar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAprobar) { Text("Aprobar") }
                }
            }
        }
    }
}

private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true, name = "Admin Solicitudes con datos")
@Composable
fun AdminSolicitudesScreenPreview() {
    val casaDeEjemplo = CasaEntity(id = 1, price = "UF 32.500", address = "Lo Barnechea, sector La Dehesa", details = "4 hab | 3 baños | 580 m²", imageUri = resourceUri(R.drawable.casa1), latitude = 0.0, longitude = 0.0, isFavorite = false)
    val solicitudesDeEjemplo = listOf(
        Solicitud(1, "cliente1@test.com", casaDeEjemplo, "15/05/2024", EstadoSolicitud.Pendiente),
        Solicitud(2, "cliente2@test.com", casaDeEjemplo, "14/05/2024", EstadoSolicitud.Aprobada),
        Solicitud(3, "cliente3@test.com", casaDeEjemplo, "13/05/2024", EstadoSolicitud.Pendiente)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Gestionar Solicitudes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(solicitudesDeEjemplo) { solicitud ->
                AdminSolicitudCard(
                    solicitud = solicitud,
                    onAprobar = {},
                    onRechazar = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Admin Solicitudes vacío")
@Composable
fun AdminSolicitudesScreenEmptyPreview() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Gestionar Solicitudes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay ninguna solicitud pendiente.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
