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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentfage.R
import com.example.rentfage.data.local.Casa
import com.example.rentfage.ui.viewmodel.EstadoSolicitud
import com.example.rentfage.ui.viewmodel.HistorialViewModel
import com.example.rentfage.ui.viewmodel.Solicitud

@Composable
fun HistorialScreen(
    historialViewModel: HistorialViewModel = viewModel()
) {
    val state by historialViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.historial_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.solicitudes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.historial_no_solicitudes),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.solicitudes) { solicitud ->
                    SolicitudCard(solicitud = solicitud)
                }
            }
        }
    }
}

@Composable
private fun SolicitudCard(solicitud: Solicitud) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.historial_solicitud_id, solicitud.id),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = solicitud.casa.address, style = MaterialTheme.typography.titleMedium)
            Text(text = solicitud.casa.price, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = stringResource(R.string.historial_fecha_label), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = solicitud.fecha)
            }
            Row {
                Text(text = stringResource(R.string.historial_estado_label), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = solicitud.estado.name)
            }
        }
    }
}

// Función auxiliar para construir la URI de un recurso drawable para las vistas previas.
private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true, name = "Historial con solicitudes")
@Composable
fun HistorialScreenPreview() {
    val casaDeEjemplo = Casa(id = 1, price = "UF 32.500", address = "Lo Barnechea, sector La Dehesa", details = "4 hab | 3 baños | 580 m²", imageUri = resourceUri(R.drawable.casa1), latitude = 0.0, longitude = 0.0, isFavorite = false)
    val solicitudesDeEjemplo = listOf(
        Solicitud(1, "test@test.com", casaDeEjemplo, "15/05/2024", EstadoSolicitud.Pendiente),
        Solicitud(2, "test@test.com", casaDeEjemplo, "14/05/2024", EstadoSolicitud.Aprobada)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.historial_title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(solicitudesDeEjemplo) { solicitud ->
                SolicitudCard(solicitud = solicitud)
            }
        }
    }
}

@Preview(showBackground = true, name = "Historial sin solicitudes")
@Composable
fun HistorialScreenEmptyPreview() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = stringResource(R.string.historial_title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.historial_no_solicitudes), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
    }
}
