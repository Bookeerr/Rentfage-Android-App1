package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentfage.data.local.Casa
import com.example.rentfage.data.local.casasDeEjemplo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CasasUiState(
    val casas: List<Casa> = emptyList()
)

// Estado para la pantalla de añadir/editar propiedad.
data class AddEditPropertyUiState(
    val address: String = "",
    val price: String = "",
    val details: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val imageUri: String? = null,
    val canSubmit: Boolean = false
)

class CasasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CasasUiState(casas = casasDeEjemplo))
    val uiState: StateFlow<CasasUiState> = _uiState.asStateFlow()

    // Flujo de estado para el formulario de añadir/editar.
    private val _addEditState = MutableStateFlow(AddEditPropertyUiState())
    val addEditState: StateFlow<AddEditPropertyUiState> = _addEditState.asStateFlow()

    fun toggleFavorite(casaId: Int) {
        _uiState.update {
            val casasActualizadas = it.casas.map {
                if (it.id == casaId) {
                    it.copy(isFavorite = !it.isFavorite)
                } else {
                    it
                }
            }
            it.copy(casas = casasActualizadas)
        }
    }

    fun deleteCasa(casaId: Int) {
        _uiState.update {
            val casasActualizadas = it.casas.filter { casa -> casa.id != casaId }
            it.copy(casas = casasActualizadas)
        }
    }

    // --- LÓGICA PARA AÑADIR/EDITAR PROPIEDADES ---

    // Prepara el formulario para editar una casa existente.
    fun loadCasaForEditing(casaId: Int) {
        val casaToEdit = _uiState.value.casas.find { it.id == casaId }
        if (casaToEdit != null) {
            _addEditState.update {
                it.copy(
                    address = casaToEdit.address,
                    price = casaToEdit.price,
                    details = casaToEdit.details,
                    latitude = casaToEdit.latitude.toString(),
                    longitude = casaToEdit.longitude.toString(),
                    imageUri = casaToEdit.imageUri
                )
            }
        } else {
            // Si no se encuentra la casa, se resetea el formulario.
            resetAddEditState()
        }
    }

    // Resetea el formulario para añadir una nueva casa.
    fun resetAddEditState() {
        _addEditState.value = AddEditPropertyUiState()
    }

    // Funciones para actualizar los campos del formulario.
    fun onAddressChange(value: String) {
        _addEditState.update { it.copy(address = value) }
        recomputeCanSubmit()
    }

    fun onPriceChange(value: String) {
        _addEditState.update { it.copy(price = value) }
        recomputeCanSubmit()
    }

    fun onDetailsChange(value: String) {
        _addEditState.update { it.copy(details = value) }
        recomputeCanSubmit()
    }

    fun onLatitudeChange(value: String) {
        _addEditState.update { it.copy(latitude = value) }
        recomputeCanSubmit()
    }

    fun onLongitudeChange(value: String) {
        _addEditState.update { it.copy(longitude = value) }
        recomputeCanSubmit()
    }

    fun onImageUriChange(uri: String?) {
        _addEditState.update { it.copy(imageUri = uri) }
        recomputeCanSubmit()
    }

    private fun recomputeCanSubmit() {
        val s = _addEditState.value
        // El botón "Guardar" solo se activa si todos los campos tienen texto y hay una imagen.
        val canSubmit = s.address.isNotBlank() &&
                        s.price.isNotBlank() &&
                        s.details.isNotBlank() &&
                        s.latitude.isNotBlank() &&
                        s.longitude.isNotBlank() &&
                        s.imageUri != null
        _addEditState.update { it.copy(canSubmit = canSubmit) }
    }

    // Guarda la propiedad (nueva o editada) solo si las validaciones son correctas.
    fun saveProperty(casaId: Int? = null) {
        if (!_addEditState.value.canSubmit) return

        if (casaId == null) {
            addCasa()
        } else {
            updateCasa(casaId)
        }
    }

    private fun addCasa() {
        val s = _addEditState.value
        val newId = (_uiState.value.casas.maxOfOrNull { c -> c.id } ?: 0) + 1
        val newCasa = Casa(
            id = newId,
            address = s.address,
            price = s.price,
            details = s.details,
            imageUri = s.imageUri!!, // Se puede usar !! porque canSubmit ya lo ha verificado.
            latitude = s.latitude.toDoubleOrNull() ?: 0.0,
            longitude = s.longitude.toDoubleOrNull() ?: 0.0,
            isFavorite = false
        )
        _uiState.update { it.copy(casas = it.casas + newCasa) }
    }

    private fun updateCasa(casaId: Int) {
        val s = _addEditState.value
        _uiState.update {
            val casasActualizadas = it.casas.map {
                if (it.id == casaId) {
                    it.copy(
                        address = s.address,
                        price = s.price,
                        details = s.details,
                        latitude = s.latitude.toDoubleOrNull() ?: 0.0,
                        longitude = s.longitude.toDoubleOrNull() ?: 0.0,
                        imageUri = s.imageUri!! // Se puede usar !! porque canSubmit ya lo ha verificado.
                    )
                } else {
                    it
                }
            }
            it.copy(casas = casasActualizadas)
        }
    }
}
