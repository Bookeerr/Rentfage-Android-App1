package com.example.rentfage.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val route: String,
    val label: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = (currentRoute == item.route),
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onPerfil: () -> Unit,
    onFavoritos: () -> Unit,
    onHistorial: () -> Unit,
    onNosotros: () -> Unit,
    onAdmin: () -> Unit,
    userRole: String?
): List<DrawerItem> {
    // Novedad: Se reordena la lista para que tenga un orden más lógico.
    val baseItems = mutableListOf(
        DrawerItem("home", "Ventas de casas", "Propiedades Disponibles", Icons.Filled.Home, onHome),
        DrawerItem("favoritos", "Mis Favoritos", "Mis Favoritos", Icons.Filled.Favorite, onFavoritos),
        DrawerItem("historial", "Historial de Solicitudes", "Historial de Solicitudes", Icons.Filled.History, onHistorial),
        DrawerItem("perfil", "Mi Perfil", "Mi Perfil", Icons.Filled.AccountCircle, onPerfil),
        DrawerItem("nosotros", "Nosotros", "Sobre Nosotros", Icons.Filled.Info, onNosotros)
    )

    if (userRole == "Admin") {
        baseItems.add(
            DrawerItem("admin_dashboard", "Panel de Administrador", "Panel de Administrador", Icons.Filled.AdminPanelSettings, onAdmin)
        )
    }

    return baseItems
}
