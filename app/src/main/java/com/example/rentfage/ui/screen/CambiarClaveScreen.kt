package com.example.rentfage.ui.screen

import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rentfage.R
import com.example.rentfage.ui.viewmodel.AuthViewModel

@Composable
fun CambiarClaveScreen(
    authViewModel: AuthViewModel = viewModel(),
    onSaveChanges: () -> Unit
) {
    val state by authViewModel.changePassword.collectAsState()
    val context = LocalContext.current

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Efecto que se dispara cuando el cambio de contraseña es exitoso.
    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show()
            authViewModel.clearChangePasswordResult() // Limpia el estado del ViewModel.
            onSaveChanges() // Navega hacia atrás.
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.VpnKey,
            contentDescription = null, // Icono decorativo.
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.change_password_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.currentPassword,
            onValueChange = authViewModel::onCurrentPasswordChange,
            label = { Text(stringResource(R.string.change_password_current_password_label)) },
            singleLine = true,
            isError = state.currentPasswordError != null,
            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                    Icon(
                        imageVector = if (showCurrentPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            supportingText = { 
                state.currentPasswordError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.newPassword,
            onValueChange = authViewModel::onNewPasswordChange,
            label = { Text(stringResource(R.string.change_password_new_password_label)) },
            singleLine = true,
            isError = state.newPasswordError != null,
            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                    Icon(
                        imageVector = if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            supportingText = { 
                state.newPasswordError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.confirmNewPassword,
            onValueChange = authViewModel::onConfirmNewPasswordChange,
            label = { Text(stringResource(R.string.change_password_confirm_new_password_label)) },
            singleLine = true,
            isError = state.confirmNewPasswordError != null,
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null
                    )
                }
            },
            supportingText = { 
                state.confirmNewPasswordError?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        state.errorMsg?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = authViewModel::submitChangePassword,
            enabled = state.canSubmit && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Guardando...") 
            } else {
                Text(stringResource(R.string.change_password_save_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CambiarClaveScreenPreview() {
    CambiarClaveScreen(onSaveChanges = {})
}
