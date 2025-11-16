package com.example.rentfage.ui.screen

import com.example.rentfage.data.local.storage.UserPreferences
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rentfage.R
import com.example.rentfage.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreenVm(
    authViewModel: AuthViewModel, // Se recibe el ViewModel
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val state by authViewModel.login.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            userPrefs.setLoggedIn(true)
            authViewModel.clearLoginResult()
            onLoginOkNavigateHome()
        }
    }

    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = authViewModel::onLoginEmailChange,
        onPassChange = authViewModel::onLoginPassChange,
        onSubmit = authViewModel::submitLogin,
        onGoRegister = onGoRegister
    )
}

@Composable
private fun LoginScreen(
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isSubmitting) 0.6f else 1f,
        label = "alphaLoginButton"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.LockOpen,
            contentDescription = null, // Icono decorativo
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.login_email_label)) },
            singleLine = true,
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            supportingText = { 
                if (emailError != null) {
                    Text(emailError, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pass,
            onValueChange = onPassChange,
            label = { Text(stringResource(R.string.login_password_label)) },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPass) stringResource(R.string.login_hide_password_cd) else stringResource(R.string.login_show_password_cd)
                    )
                }
            },
            isError = passError != null,
            supportingText = { 
                if (passError != null) {
                    Text(passError, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = canSubmit && !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .alpha(buttonAlpha)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.login_validating_button))
            } else {
                Text(stringResource(R.string.login_submit_button))
            }
        }

        if (errorMsg != null) {
            Spacer(Modifier.height(12.dp))
            Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onGoRegister,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(stringResource(R.string.login_create_account_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        email = "",
        pass = "",
        emailError = "Error de ejemplo",
        passError = null,
        canSubmit = true,
        isSubmitting = false,
        errorMsg = null,
        onEmailChange = {},
        onPassChange = {},
        onSubmit = {},
        onGoRegister = {}
    )
}
