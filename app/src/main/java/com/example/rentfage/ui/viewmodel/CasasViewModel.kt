package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.data.repository.CasasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CasasUiState(
    val casas: List<CasaEntity> = emptyList()
)

// Estado para la pantalla de añadir/editar propiedad.
data class AddEditPropertyUiState(
    val address: String = "",
    val price: String = "",
    val details: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val imageUri: String? = null,
    val canSubmit: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class CasasViewModel(private val repository: CasasRepository) : ViewModel() {

    // --- ESTADOS DE UI ---
    val uiState: StateFlow<CasasUiState> = repository.todasLasCasas
        .map { casasList -> CasasUiState(casas = casasList) }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = CasasUiState())

    val favoritasUiState: StateFlow<CasasUiState> = repository.casasFavoritas
        .map { casasList -> CasasUiState(casas = casasList) }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = CasasUiState())

    private val _addEditState = MutableStateFlow(AddEditPropertyUiState())
    val addEditState: StateFlow<AddEditPropertyUiState> = _addEditState.asStateFlow()

    // --- FUNCIONES DE USUARIO ---
    fun toggleFavorite(casa: CasaEntity) {
        viewModelScope.launch {
            val casaActualizada = casa.copy(isFavorite = !casa.isFavorite)
            repository.actualizarCasa(casaActualizada)
        }
    }

    fun getCasaById(id: Int): StateFlow<CasaEntity?> {
        return repository.getById(id)
            .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = null)
    }

    // --- FUNCIONES DE ADMINISTRADOR ---

    fun deleteCasa(casa: CasaEntity) {
        viewModelScope.launch {
            repository.borrarCasa(casa)
        }
    }

    fun saveProperty(casaId: Int?) {
        if (!_addEditState.value.canSubmit) return

        viewModelScope.launch {
            _addEditState.update { it.copy(isSaving = true) }

            val s = _addEditState.value
            val casaEntity = CasaEntity(
                id = casaId ?: 0, // Room se encarga si es 0
                address = s.address,
                price = s.price,
                details = s.details,
                imageUri = s.imageUri!!,
                latitude = s.latitude.toDoubleOrNull() ?: 0.0,
                longitude = s.longitude.toDoubleOrNull() ?: 0.0
            )

            if (casaId == null) {
                repository.insertarCasa(casaEntity.copy(id = 0)) // Asegurarse de que el ID es 0 para autogenerar
            } else {
                repository.actualizarCasa(casaEntity)
            }

            _addEditState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    // --- MANEJO DEL FORMULARIO DE AÑADIR/EDITAR ---

    private fun populateFormFromEntity(casa: CasaEntity) {
        _addEditState.update {
            it.copy(
                address = casa.address,
                price = casa.price,
                details = casa.details,
                latitude = casa.latitude.toString(),
                longitude = casa.longitude.toString(),
                imageUri = casa.imageUri
            )
        }
    }

    suspend fun loadCasaForEditingById(id: Int) {
        val casa = repository.getById(id).firstOrNull()
        if (casa != null) {
            populateFormFromEntity(casa)
        }
    }

    fun resetAddEditState() {
        _addEditState.value = AddEditPropertyUiState()
    }

    fun onAddressChange(value: String) { _addEditState.update { it.copy(address = value) }; recomputeCanSubmit() }
    fun onPriceChange(value: String) { _addEditState.update { it.copy(price = value) }; recomputeCanSubmit() }
    fun onDetailsChange(value: String) { _addEditState.update { it.copy(details = value) }; recomputeCanSubmit() }
    fun onLatitudeChange(value: String) { _addEditState.update { it.copy(latitude = value) }; recomputeCanSubmit() }
    fun onLongitudeChange(value: String) { _addEditState.update { it.copy(longitude = value) }; recomputeCanSubmit() }
    fun onImageUriChange(uri: String?) { _addEditState.update { it.copy(imageUri = uri) }; recomputeCanSubmit() }

    private fun recomputeCanSubmit() {
        val s = _addEditState.value
        val canSubmit = s.address.isNotBlank() && s.price.isNotBlank() && s.details.isNotBlank() &&
                s.latitude.isNotBlank() && s.longitude.isNotBlank() && s.imageUri != null
        _addEditState.update { it.copy(canSubmit = canSubmit) }
    }
}