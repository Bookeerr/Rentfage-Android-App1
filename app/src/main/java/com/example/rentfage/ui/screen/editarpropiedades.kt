package com.example.rentfage.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rentfage.ui.viewmodel.CasasViewModel

@Composable
fun AddEditPropertyScreen(
    casasViewModel: CasasViewModel,
    casaId: Int?,
    onNavigateBack: () -> Unit
) {
    val uiState by casasViewModel.addEditState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> casasViewModel.onImageUriChange(uri?.toString()) }
    )

    LaunchedEffect(key1 = casaId) {
        if (casaId != null) {
            casasViewModel.loadCasaForEditing(casaId)
        } else {
            casasViewModel.resetAddEditState()
        }
    }

    AddEditPropertyContent(
        address = uiState.address,
        price = uiState.price,
        details = uiState.details,
        latitude = uiState.latitude,
        longitude = uiState.longitude,
        imageUri = uiState.imageUri,
        canSubmit = uiState.canSubmit,
        isEditing = casaId != null,
        onAddressChange = casasViewModel::onAddressChange,
        onPriceChange = casasViewModel::onPriceChange,
        onDetailsChange = casasViewModel::onDetailsChange,
        onLatitudeChange = casasViewModel::onLatitudeChange,
        onLongitudeChange = casasViewModel::onLongitudeChange,
        onImageChange = { imagePickerLauncher.launch("image/*") },
        onSaveClick = {
            casasViewModel.saveProperty(casaId)
            onNavigateBack()
        }
    )
}

@Composable
private fun AddEditPropertyContent(
    isEditing: Boolean,
    address: String,
    price: String,
    details: String,
    latitude: String,
    longitude: String,
    imageUri: String?,
    canSubmit: Boolean,
    onAddressChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onImageChange: () -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEditing) "Editar Propiedad" else "Añadir Nueva Propiedad",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Imagen de la propiedad",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedButton(onClick = onImageChange, modifier = Modifier.fillMaxWidth()) {
                Text(if (imageUri == null) "Seleccionar Imagen" else "Cambiar Imagen")
            }

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Precio (ej: UF 28.500)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = details,
                onValueChange = onDetailsChange,
                label = { Text("Detalles (ej: 4 hab | 2 baños | 450 m²)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChange,
                label = { Text("Latitud") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("Longitud") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = canSubmit
            ) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true, name = "Añadir Propiedad")
@Composable
fun AddPropertyPreview() {
    AddEditPropertyContent(
        isEditing = false,
        address = "",
        price = "",
        details = "",
        latitude = "",
        longitude = "",
        imageUri = null,
        canSubmit = false,
        onAddressChange = {},
        onPriceChange = {},
        onDetailsChange = {},
        onLatitudeChange = {},
        onLongitudeChange = {},
        onImageChange = {},
        onSaveClick = {}
    )
}

@Preview(showBackground = true, name = "Editar Propiedad")
@Composable
fun EditPropertyPreview() {
    AddEditPropertyContent(
        isEditing = true,
        address = "Av. Siempre Viva 123",
        price = "UF 15.000",
        details = "3 hab | 2 baños | 200 m²",
        latitude = "-33.456",
        longitude = "-70.678",
        imageUri = "",
        canSubmit = true,
        onAddressChange = {},
        onPriceChange = {},
        onDetailsChange = {},
        onLatitudeChange = {},
        onLongitudeChange = {},
        onImageChange = {},
        onSaveClick = {}
    )
}
