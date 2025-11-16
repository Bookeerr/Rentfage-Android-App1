package com.example.rentfage.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.rentfage.R
import com.example.rentfage.ui.viewmodel.AuthViewModel
import com.example.rentfage.ui.viewmodel.PerfilViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}

private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
fun PerfilScreenVm(
    authViewModel: AuthViewModel, 
    perfilViewModel: PerfilViewModel, // Se recibe el ViewModel, ya no se crea aquí.
    onLogout: () -> Unit, 
    onEditProfile: () -> Unit, 
    onChangePassword: () -> Unit
) {
    // Se recargan los datos del usuario cada vez que esta pantalla aparece.
    LaunchedEffect(Unit) {
        perfilViewModel.cargarDatosUsuario()
    }
    
    val state by perfilViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            Toast.makeText(context, R.string.profile_picture_success_toast, Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, R.string.profile_picture_error_toast, Toast.LENGTH_SHORT).show()
        }
    }
    var showDialog by remember { mutableStateOf(false) }

    val onTakePicture = { 
        val file = createTempImageFile(context)
        val uri = getImageUriForFile(context, file)
        pendingCaptureUri = uri
        takePictureLauncher.launch(uri)
    }

    val onDeletePicture = {
        photoUriString = null
        showDialog = false
        Toast.makeText(context, R.string.profile_picture_deleted_toast, Toast.LENGTH_SHORT).show()
    }

    PerfilScreen(
        name = state.name,
        email = state.email,
        phone = state.phone,
        initials = state.initials,
        photoUriString = photoUriString,
        showDialog = showDialog,
        onShowDialogChange = { showDialog = it },
        onTakePicture = onTakePicture,
        onDeletePicture = onDeletePicture,
        onEditProfile = onEditProfile,
        onChangePassword = onChangePassword,
        onLogout = {
            authViewModel.logout()
            onLogout()
        }
    )
}

@Composable
private fun PerfilScreen(
    name: String, email: String, phone: String, initials: String,
    photoUriString: String?, showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit, onTakePicture: () -> Unit, onDeletePicture: () -> Unit,
    onEditProfile: () -> Unit, onChangePassword: () -> Unit, onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Información de Usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier.size(90.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = initials, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = name, style = MaterialTheme.typography.headlineSmall)
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = stringResource(R.string.profile_email_cd), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(text = email, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = stringResource(R.string.profile_phone_cd), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(text = phone, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Acciones de la Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        
                        if (photoUriString != null) {
                            AsyncImage(
                                model = Uri.parse(photoUriString),
                                contentDescription = stringResource(R.string.profile_picture_taken_cd),
                                modifier = Modifier.fillMaxWidth().height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Button(onClick = onTakePicture, modifier = Modifier.fillMaxWidth()) {
                            Text(if (photoUriString.isNullOrEmpty()) stringResource(R.string.profile_open_camera_button) else stringResource(R.string.profile_retake_picture_button))
                        }
                        if (!photoUriString.isNullOrEmpty()) {
                            OutlinedButton(onClick = { onShowDialogChange(true) }, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.profile_delete_picture_button))
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                        Button(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.profile_edit_profile_button))
                        }
                        Button(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.profile_change_password_button))
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(stringResource(R.string.profile_logout_button))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onShowDialogChange(false) },
            title = { Text(stringResource(R.string.profile_delete_confirmation_title)) },
            text = { Text(stringResource(R.string.profile_delete_confirmation_message)) },
            confirmButton = {
                TextButton(onClick = onDeletePicture) {
                    Text(stringResource(R.string.profile_accept_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowDialogChange(false) }) {
                    Text(stringResource(R.string.profile_cancel_button))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen(
        name = "Quintana Reyes",
        email = "quintana.reyes@gmail.com",
        phone = "+56 9 8765 4321",
        initials = "QR",
        photoUriString = null,
        showDialog = false,
        onShowDialogChange = {},
        onTakePicture = {},
        onDeletePicture = {},
        onEditProfile = {},
        onChangePassword = {},
        onLogout = {}
    )
}
