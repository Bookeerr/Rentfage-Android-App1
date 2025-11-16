package com.example.rentfage.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rentfage.R
import com.example.rentfage.ui.viewmodel.AuthViewModel

// Composable "ViewModel" que gestiona la lógica y el estado de la pantalla de registro.
@Composable
fun RegisterScreenVm(
    authViewModel: AuthViewModel, // Se recibe la instancia compartida del ViewModel de autenticación.
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    // Recoge el estado de la UI del flujo de registro en el ViewModel.
    val state by authViewModel.register.collectAsStateWithLifecycle()

    // Efecto que se dispara cuando el registro es exitoso para navegar a la pantalla de login.
    LaunchedEffect(state.success) {
        if (state.success) {
            authViewModel.clearRegisterResult() // Limpia el estado para evitar la navegación al volver.
            onRegisteredNavigateLogin()
        }
    }

    // Llama al composable de la UI, pasándole el estado y las acciones.
    RegisterScreen(
        name = state.name,
        email = state.email,
        phone = state.phone,
        pass = state.pass,
        confirm = state.confirm,
        nameError = state.nameError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onNameChange = authViewModel::onNameChange,
        onEmailChange = authViewModel::onRegisterEmailChange,
        onPhoneChange = authViewModel::onPhoneChange,
        onPassChange = authViewModel::onRegisterPassChange,
        onConfirmChange = authViewModel::onConfirmChange,
        onSubmit = authViewModel::submitRegister,
        onGoLogin = onGoLogin
    )
}

// Composable que define la interfaz de usuario (UI) de la pantalla de registro.
@Composable
private fun RegisterScreen(
    name: String,
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    nameError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    // Estados para controlar la visibilidad de las contraseñas.
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null, // Icono decorativo.
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(24.dp))

        // Campo de texto para el nombre.
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.register_name_label)) },
            singleLine = true,
            isError = nameError != null,
            supportingText = { 
                nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para el email.
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.login_email_label)) },
            singleLine = true,
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            supportingText = {
                emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para el teléfono.
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text(stringResource(R.string.register_phone_label)) },
            singleLine = true,
            isError = phoneError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            supportingText = {
                phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para la contraseña.
        OutlinedTextField(
            value = pass,
            onValueChange = onPassChange,
            label = { Text(stringResource(R.string.login_password_label)) },
            singleLine = true,
            isError = passError != null,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPass) stringResource(R.string.login_hide_password_cd) else stringResource(R.string.login_show_password_cd)
                    )
                }
            },
            supportingText = {
                passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para confirmar la contraseña.
        OutlinedTextField(
            value = confirm,
            onValueChange = onConfirmChange,
            label = { Text(stringResource(R.string.register_confirm_password_label)) },
            singleLine = true,
            isError = confirmError != null,
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirm = !showConfirm }) {
                    Icon(
                        imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showConfirm) stringResource(R.string.register_hide_confirm_password_cd) else stringResource(R.string.register_show_confirm_password_cd)
                    )
                }
            },
            supportingText = {
                confirmError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Botón principal para enviar el formulario de registro.
        Button(
            onClick = onSubmit,
            enabled = canSubmit && !isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.register_creating_account_button))
            } else {
                Text(stringResource(R.string.register_submit_button))
            }
        }

        if (errorMsg != null) {
            Spacer(Modifier.height(12.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))

        // Botón secundario para navegar a la pantalla de login.
        OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text(stringResource(R.string.register_go_to_login_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        name = "",
        email = "",
        phone = "",
        pass = "",
        confirm = "",
        nameError = "Error de ejemplo",
        emailError = null,
        phoneError = null,
        passError = null,
        confirmError = null,
        canSubmit = true,
        isSubmitting = false,
        errorMsg = null,
        onNameChange = {},
        onEmailChange = {},
        onPhoneChange = {},
        onPassChange = {},
        onConfirmChange = {},
        onSubmit = {},
        onGoLogin = {}
    )
}
