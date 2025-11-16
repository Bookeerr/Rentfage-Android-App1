package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rentfage.domain.validation.validateNameLettersOnly
import com.example.rentfage.domain.validation.validatePhoneDigitsOnly
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val name: String = "Usuario no encontrado",
    val email: String = "",
    val phone: String = "",
    val initials: String = "--"
)

data class EditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val canSubmit: Boolean = false
)

class PerfilViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _editProfileState = MutableStateFlow(EditProfileUiState())
    val editProfileState: StateFlow<EditProfileUiState> = _editProfileState

    init {
        cargarDatosUsuario()
    }

    fun cargarDatosUsuario() {
        val emailUsuarioActivo = AuthViewModel.activeUserEmail
        if (emailUsuarioActivo != null) {
            val usuarioEncontrado = AuthViewModel.USERS.find { it.email.equals(emailUsuarioActivo, ignoreCase = true) }
            if (usuarioEncontrado != null) {
                _uiState.value = PerfilUiState(
                    name = usuarioEncontrado.name,
                    email = usuarioEncontrado.email,
                    phone = usuarioEncontrado.phone,
                    initials = getInitials(usuarioEncontrado.name)
                )
                _editProfileState.value = EditProfileUiState(
                    name = usuarioEncontrado.name,
                    phone = usuarioEncontrado.phone
                )
            }
        }
    }

    fun onEditNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _editProfileState.update {
            it.copy(name = filtered, nameError = validateNameLettersOnly(filtered))
        }
        recomputeEditCanSubmit()
    }

    fun onEditPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        if (digitsOnly.length <= 8) {
            _editProfileState.update {
                it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
            }
            recomputeEditCanSubmit()
        }
    }

    private fun recomputeEditCanSubmit() {
        val s = _editProfileState.value
        val noErrors = s.nameError == null && s.phoneError == null
        val filled = s.name.isNotBlank() && s.phone.isNotBlank()
        _editProfileState.update { it.copy(canSubmit = noErrors && filled) }
    }

    // La función ahora acepta una acción a ejecutar cuando la actualización termina.
    fun updateUser(onUpdateFinished: () -> Unit) {
        val editState = _editProfileState.value
        if (!editState.canSubmit) return

        viewModelScope.launch {
            val emailUsuarioActivo = AuthViewModel.activeUserEmail
            if (emailUsuarioActivo != null) {
                val userIndex = AuthViewModel.USERS.indexOfFirst { it.email.equals(emailUsuarioActivo, ignoreCase = true) }
                if (userIndex != -1) {
                    val currentUser = AuthViewModel.USERS[userIndex]
                    val updatedUser = currentUser.copy(name = editState.name, phone = editState.phone)
                    AuthViewModel.USERS[userIndex] = updatedUser
                    
                    cargarDatosUsuario()
                    // Se ejecuta la acción (la navegación) solo después de guardar.
                    onUpdateFinished()
                }
            }
        }
    }

    private fun getInitials(name: String): String {
        return name.split(' ')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
    }
}
