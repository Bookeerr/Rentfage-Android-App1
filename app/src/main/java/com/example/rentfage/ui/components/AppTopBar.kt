package com.example.rentfage.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String, // Novedad: Se añade el título.
    onOpenDrawer: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) }, // Novedad: Se muestra el título.
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Abrir menú")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}
