package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentfage.data.local.entity.CasaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class EstadoSolicitud { Pendiente, Aprobada, Rechazada }

data class Solicitud(
    val id: Int,
    val usuarioEmail: String,
    val casa: CasaEntity, // <-- Usa la entidad correcta de la BD
    val fecha: String,
    var estado: EstadoSolicitud
)

data class HistorialUiState(
    val solicitudes: List<Solicitud> = emptyList()
)

class HistorialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    companion object {
        // Temporal: Se usa una lista global para simular la persistencia de solicitudes.
        private val solicitudesGlobales = mutableListOf<Solicitud>()
    }

    init {
        viewModelScope.launch {
            cargarSolicitudesDeUsuario()
        }
    }

    fun addSolicitud(casa: CasaEntity) {
        val currentUserEmail = AuthViewModel.activeUserEmail
        if (currentUserEmail != null) {
            val newId = (solicitudesGlobales.maxOfOrNull { it.id } ?: 0) + 1
            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val nuevaSolicitud = Solicitud(
                id = newId,
                usuarioEmail = currentUserEmail,
                casa = casa,
                fecha = fechaActual,
                estado = EstadoSolicitud.Pendiente
            )

            solicitudesGlobales.add(nuevaSolicitud)
            cargarSolicitudesDeUsuario()
        }
    }

    fun cargarSolicitudesDeUsuario() {
        val currentUserEmail = AuthViewModel.activeUserEmail
        if (currentUserEmail != null) {
            _uiState.update {
                it.copy(solicitudes = solicitudesGlobales.filter { s -> s.usuarioEmail == currentUserEmail })
            }
        } else {
            _uiState.update { it.copy(solicitudes = emptyList()) }
        }
    }

    // --- Funciones de Admin ---

    fun cargarTodasLasSolicitudes() {
        _uiState.update { it.copy(solicitudes = solicitudesGlobales) }
    }

    fun aprobarSolicitud(solicitudId: Int) {
        val solicitud = solicitudesGlobales.find { it.id == solicitudId }
        solicitud?.estado = EstadoSolicitud.Aprobada
        cargarTodasLasSolicitudes()
    }

    fun rechazarSolicitud(solicitudId: Int) {
        val solicitud = solicitudesGlobales.find { it.id == solicitudId }
        solicitud?.estado = EstadoSolicitud.Rechazada
        cargarTodasLasSolicitudes()
    }
}
