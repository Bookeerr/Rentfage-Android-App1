package com.example.rentfage.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentfage.ui.viewmodel.CasasViewModel

@Composable
fun AdminDashboardScreen(
    casasViewModel: CasasViewModel,
    onGoToPropertyList: () -> Unit,
    onGoToSolicitudes: () -> Unit
) {
    val casasState by casasViewModel.uiState.collectAsState()
    val totalCasas = casasState.casas.size

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Panel de Administrador",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Estadísticas", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(text = "Total de Propiedades: ", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "$totalCasas", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Herramientas", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onGoToPropertyList, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestionar Propiedades")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onGoToSolicitudes, modifier = Modifier.fillMaxWidth()) {
                    Text("Gestionar Solicitudes")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    val casasViewModel: CasasViewModel = viewModel()
    AdminDashboardScreen(
        casasViewModel = casasViewModel,
        onGoToPropertyList = {},
        onGoToSolicitudes = {}
    )
}
