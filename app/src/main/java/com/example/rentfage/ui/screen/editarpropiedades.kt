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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rentfage.data.local.room.AppDatabase
import com.example.rentfage.data.repository.CasasRepository
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.example.rentfage.ui.viewmodel.CasasViewModelFactory

// Nueva puerta de entrada que se encarga de la logica del ViewModel.
@Composable
fun AddEditPropertyScreenVm(
    casaId: Int?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CasasRepository(database.casaDao()) }
    val factory = remember { CasasViewModelFactory(repository) }
    val vm: CasasViewModel = viewModel(factory = factory)

    // Se observa el estado del formulario desde el ViewModel.
    val uiState by vm.addEditState.collectAsStateWithLifecycle()

    // Efecto para cargar los datos de la casa si estamos editando.
    LaunchedEffect(key1 = casaId) {
        if (casaId != null) {
            // Primero, obtenemos la casa completa desde la BD.
            vm.getCasaById(casaId).collect { casaEntity ->
                if (casaEntity != null) {
                    // Una vez que la tenemos, la cargamos en el formulario.
                    vm.loadCasaForEditing(casaEntity)
                }
            }
        } else {
            // Si es una casa nueva, reseteamos el formulario.
            vm.resetAddEditState()
        }
    }
    
    // Efecto para navegar hacia atras cuando el guardado es exitoso.
    LaunchedEffect(key1 = uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
            vm.resetAddEditState() // Limpiamos el estado para la proxima vez.
        }
    }

    AddEditPropertyContent(
        isEditing = casaId != null,
        address = uiState.address,
        price = uiState.price,
        details = uiState.details,
        latitude = uiState.latitude,
        longitude = uiState.longitude,
        imageUri = uiState.imageUri,
        canSubmit = uiState.canSubmit,
        isSaving = uiState.isSaving,
        onAddressChange = vm::onAddressChange,
        onPriceChange = vm::onPriceChange,
        onDetailsChange = vm::onDetailsChange,
        onLatitudeChange = vm::onLatitudeChange,
        onLongitudeChange = vm::onLongitudeChange,
        onImageChange = { vm.onImageUriChange(it?.toString()) },
        onSaveClick = { vm.saveProperty(casaId) }
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
    isSaving: Boolean,
    onAddressChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onImageChange: (Uri?) -> Unit,
    onSaveClick: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> onImageChange(uri) }
    )

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

            OutlinedButton(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (imageUri == null) "Seleccionar Imagen" else "Cambiar Imagen")
            }

            OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = price, onValueChange = onPriceChange, label = { Text("Precio (ej: UF 28.500)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = details, onValueChange = onDetailsChange, label = { Text("Detalles (ej: 4 hab | 2 baños | 450 m²)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = latitude, onValueChange = onLatitudeChange, label = { Text("Latitud") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            OutlinedTextField(value = longitude, onValueChange = onLongitudeChange, label = { Text("Longitud") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth(), enabled = canSubmit && !isSaving) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Añadir Propiedad")
@Composable
fun AddPropertyPreview() {
    AddEditPropertyContent(isEditing = false, address = "", price = "", details = "", latitude = "", longitude = "", imageUri = null, canSubmit = false, isSaving = false, onAddressChange = {}, onPriceChange = {}, onDetailsChange = {}, onLatitudeChange = {}, onLongitudeChange = {}, onImageChange = {}, onSaveClick = {})
}

@Preview(showBackground = true, name = "Editar Propiedad")
@Composable
fun EditPropertyPreview() {
    AddEditPropertyContent(isEditing = true, address = "Av. Siempre Viva 123", price = "UF 15.000", details = "3 hab | 2 baños | 200 m²", latitude = "-33.456", longitude = "-70.678", imageUri = null, canSubmit = true, isSaving = false, onAddressChange = {}, onPriceChange = {}, onDetailsChange = {}, onLatitudeChange = {}, onLongitudeChange = {}, onImageChange = {}, onSaveClick = {})
}
