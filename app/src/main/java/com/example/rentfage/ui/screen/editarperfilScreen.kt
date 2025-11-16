package com.example.rentfage.ui.screen

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rentfage.R
import com.example.rentfage.ui.viewmodel.PerfilViewModel

@Composable
fun editarperfilScreen(
    perfilViewModel: PerfilViewModel, // Se recibe el ViewModel, ya no se crea aquí.
    onSaveChanges: () -> Unit
) {
    val editState by perfilViewModel.editProfileState.collectAsState()

    LaunchedEffect(Unit) {
        perfilViewModel.cargarDatosUsuario()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_profile_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editState.name,
                onValueChange = perfilViewModel::onEditNameChange,
                label = { Text(stringResource(R.string.edit_profile_name_label)) },
                isError = editState.nameError != null,
                supportingText = {
                    editState.nameError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = editState.phone,
                onValueChange = perfilViewModel::onEditPhoneChange,
                label = { Text(stringResource(R.string.edit_profile_phone_label)) },
                isError = editState.phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    editState.phoneError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    perfilViewModel.updateUser(onUpdateFinished = onSaveChanges)
                },
                enabled = editState.canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.edit_profile_save_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarPerfilScreenPreview() {
    // Esta pantalla ahora necesita que se le pase un ViewModel para ser previsualizada.
}
