package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentfage.data.local.Casa
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
    val casa: Casa,
    val fecha: String,
    var estado: EstadoSolicitud // Se cambia a var para poder modificar el estado
)

data class HistorialUiState(
    val solicitudes: List<Solicitud> = emptyList()
)

class HistorialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    companion object {
        private val solicitudesGlobales = mutableListOf<Solicitud>()
    }

    init {
        // Cuando el ViewModel se crea, intenta cargar las solicitudes del usuario.
        viewModelScope.launch {
            cargarSolicitudesDeUsuario()
        }
    }

    fun addSolicitud(casa: Casa) {
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
            // Se notifica a la UI que la lista ha cambiado.
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

    // funciones de admin

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
