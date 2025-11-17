package com.example.rentfage.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentfage.data.local.room.AppDatabase
import com.example.rentfage.data.local.room.entity.CasaEntity
import com.example.rentfage.data.repository.CasasRepository
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.example.rentfage.ui.viewmodel.CasasViewModelFactory

// Nueva puerta de entrada que se encarga de la logica del ViewModel.
@Composable
fun AdminPropertyListScreenVm(
    onAddProperty: () -> Unit,
    onEditProperty: (Int) -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CasasRepository(database.casaDao()) }
    val factory = remember { CasasViewModelFactory(repository) }
    val vm: CasasViewModel = viewModel(factory = factory)

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    AdminPropertyListScreen(
        casas = uiState.casas,
        onAddProperty = onAddProperty,
        onEditProperty = onEditProperty,
        onDeleteProperty = { casa -> vm.deleteCasa(casa) } // Conectamos la accion de borrar.
    )
}

@Composable
private fun AdminPropertyListScreen(
    casas: List<CasaEntity>,
    onAddProperty: () -> Unit,
    onEditProperty: (Int) -> Unit,
    onDeleteProperty: (CasaEntity) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var propertyToDelete by remember { mutableStateOf<CasaEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProperty) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Propiedad")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = "Gestionar Propiedades", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(casas) { casa ->
                PropertyListItem(
                    casa = casa,
                    onDeleteClick = {
                        propertyToDelete = casa
                        showDeleteDialog = true
                    },
                    onEditClick = { onEditProperty(casa.id) }
                )
            }
        }
    }

    if (showDeleteDialog && propertyToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la propiedad en ${propertyToDelete!!.address}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteProperty(propertyToDelete!!)
                        showDeleteDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PropertyListItem(
    casa: CasaEntity,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = casa.address, style = MaterialTheme.typography.titleMedium)
                Text(text = casa.price, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row {
                Button(onClick = onEditClick) { Text("Editar") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDeleteClick) { Text("Eliminar") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPropertyListScreenPreview() {
    AdminPropertyListScreen(
        casas = listOf(
            CasaEntity(id = 1, address = "Av. Vitacura 123", price = "UF 30.000", details = "", imageUri = "", latitude = 0.0, longitude = 0.0),
            CasaEntity(id = 2, address = "Las Condes 456", price = "UF 25.000", details = "", imageUri = "", latitude = 0.0, longitude = 0.0)
        ),
        onAddProperty = {},
        onEditProperty = {},
        onDeleteProperty = {}
    )
}
