package com.example.rentfage.ui.screen

import android.content.ContentResolver
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rentfage.R
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.example.rentfage.ui.viewmodel.HistorialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetalleCasaScreenVm(
    casaId: Int,
    onGoHome: () -> Unit,
    historialViewModel: HistorialViewModel,
    casasViewModel: CasasViewModel
) {
    val casaState by casasViewModel.getCasaById(casaId).collectAsStateWithLifecycle()

    Crossfade(targetState = casaState, label = "DetalleCasaAnimation") { casa ->
        if (casa != null) {
            DetalleCasaContent(
                casa = casa,
                onGoHome = onGoHome,
                onAddSolicitud = { historialViewModel.addSolicitud(casa) }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando propiedad...")
            }
        }
    }
}

@Composable
private fun DetalleCasaContent(
    casa: CasaEntity,
    onGoHome: () -> Unit,
    onAddSolicitud: () -> Unit
) {
    var showPurchaseSummary by rememberSaveable { mutableStateOf(false) }
    var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Crossfade(targetState = showPurchaseSummary, label = "PurchaseScreenAnimation") { isSummaryVisible ->
        if (isSummaryVisible) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("¡Solicitud Enviada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onGoHome) { Text("Volver a propiedades") }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = casa.imageUri.toUri(),
                    contentDescription = "Imagen de la casa",
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = casa.price, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = casa.address, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = casa.details, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comprar esta propiedad")
                }
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmacion de Compra") },
            text = { Text("¿Estas seguro de comprar esta propiedad?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            onAddSolicitud()
                            showConfirmationDialog = false
                            delay(300)
                            showPurchaseSummary = true
                        }
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

private fun resourceUri(resourceId: Int): String {
    return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://com.example.rentfage/drawable/$resourceId"
}

@Preview(showBackground = true)
@Composable
fun DetalleCasaScreenPreview() {
    val casaDeEjemplo = CasaEntity(1, "UF 28.500", "Av. Vitacura, Vitacura, Santiago", "4 hab | 1 baño | 450 m²", resourceUri(R.drawable.casa1), -33.4130, -70.5947)
    DetalleCasaContent(casa = casaDeEjemplo, onGoHome = {}, onAddSolicitud = {})
}
