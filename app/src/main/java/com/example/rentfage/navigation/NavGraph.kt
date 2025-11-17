package com.example.rentfage.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.rentfage.data.local.database.AppDatabase
import com.example.rentfage.data.local.storage.UserPreferences
import com.example.rentfage.data.repository.CasasRepository
import com.example.rentfage.ui.components.AppDrawer
import com.example.rentfage.ui.components.AppTopBar
import com.example.rentfage.ui.components.defaultDrawerItems
import com.example.rentfage.ui.screen.*
import com.example.rentfage.ui.viewmodel.AuthViewModel
import com.example.rentfage.ui.viewmodel.CasasViewModel
import com.example.rentfage.ui.viewmodel.CasasViewModelFactory
import com.example.rentfage.ui.viewmodel.HistorialViewModel
import com.example.rentfage.ui.viewmodel.PerfilViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // --- CONTEXTO Y PREFERENCIAS ---
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val userRole by userPreferences.userRole.collectAsState(initial = null)

    // --- VIEWMODELS COMPARTIDOS ---
    val perfilViewModel: PerfilViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val historialViewModel: HistorialViewModel = viewModel()

    // Crear y compartir el ViewModel de Casas
    val database = remember { AppDatabase.getInstance(context) }
    val casasRepository = remember { CasasRepository(database.casaDao()) }
    val casasViewModelFactory = remember { CasasViewModelFactory(casasRepository) }
    val casasViewModel: CasasViewModel = viewModel(factory = casasViewModelFactory)

    val showTopBar = currentRoute != "login" && currentRoute != "register"

    // --- ACCIONES DE NAVEGACIÓN ---
    val goHome: () -> Unit = { navController.navigate("home") }
    val goLogin: () -> Unit = { navController.navigate("login") }
    val goRegister: () -> Unit = { navController.navigate("register") }
    val goPerfil: () -> Unit = { navController.navigate("perfil") }
    val goEditProfile: () -> Unit = { navController.navigate("edit_profile") }
    val goChangePassword: () -> Unit = { navController.navigate("change_password") }
    val goHistorial: () -> Unit = { navController.navigate("historial") }
    val goFavoritos: () -> Unit = { navController.navigate("favoritos") }
    val goNosotros: () -> Unit = { navController.navigate("nosotros") }
    val goAdminDashboard: () -> Unit = { navController.navigate("admin_dashboard") }
    val goAdminPropertyList: () -> Unit = { navController.navigate("admin_property_list") }
    val goAdminSolicitudes: () -> Unit = { navController.navigate("admin_solicitudes") }
    val onHouseClick: (Int) -> Unit = { casaId -> navController.navigate("detalle_casa/$casaId") }
    val onNavigateBack: () -> Unit = { navController.popBackStack() }
    val goAddEditProperty: (Int?) -> Unit = { casaId ->
        val route = if (casaId != null) "add_edit_property/$casaId" else "add_edit_property/-1"
        navController.navigate(route)
    }

    val drawerItems = defaultDrawerItems(
        onHome = { scope.launch { drawerState.close() }; goHome() },
        onPerfil = { scope.launch { drawerState.close() }; goPerfil() },
        onFavoritos = { scope.launch { drawerState.close() }; goFavoritos() },
        onHistorial = { scope.launch { drawerState.close() }; goHistorial() },
        onNosotros = { scope.launch { drawerState.close() }; goNosotros() },
        onAdmin = { scope.launch { drawerState.close() }; goAdminDashboard() },
        userRole = userRole
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showTopBar,
        drawerContent = { AppDrawer(currentRoute = currentRoute, items = drawerItems) }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    val title = drawerItems.find { it.route == currentRoute }?.title ?: ""
                    AppTopBar(title = title, onOpenDrawer = { scope.launch { drawerState.open() } })
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 300 }) },
                exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -300 }) }
            ) {

                composable("home") { HomeScreenVm(onHouseClick = onHouseClick, casasViewModel = casasViewModel) }
                composable("login") { LoginScreenVm(authViewModel = authViewModel, onLoginOkNavigateHome = goHome, onGoRegister = goRegister) }
                composable("register") { RegisterScreenVm(authViewModel = authViewModel, onRegisteredNavigateLogin = goLogin, onGoLogin = goLogin) }
                composable("perfil") { PerfilScreenVm(authViewModel = authViewModel, perfilViewModel = perfilViewModel, onLogout = goLogin, onEditProfile = goEditProfile, onChangePassword = goChangePassword) }

                composable(
                    route = "detalle_casa/{casaId}",
                    arguments = listOf(navArgument("casaId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val casaId = backStackEntry.arguments?.getInt("casaId") ?: 0
                    DetalleCasaScreenVm(casaId = casaId, onGoHome = goHome, historialViewModel = historialViewModel, casasViewModel = casasViewModel)
                }

                composable("favoritos") { FavoritosScreenVm(onHouseClick = onHouseClick, casasViewModel = casasViewModel) }
                composable("nosotros") { NosotrosScreen() }
                composable("historial") { HistorialScreen(historialViewModel = historialViewModel) }

                // --- RUTAS DE ADMINISTRADOR ---
                composable("admin_dashboard") {
                    AdminDashboardScreen(
                        casasViewModel = casasViewModel,
                        onGoToPropertyList = goAdminPropertyList,
                        onGoToSolicitudes = goAdminSolicitudes
                    )
                }

                composable("admin_property_list") {
                    AdminPropertyListScreenVm(
                        onAddProperty = { goAddEditProperty(null) },
                        onEditProperty = { casaId -> goAddEditProperty(casaId) },
                        casasViewModel = casasViewModel
                    )
                }

                composable(
                    route = "add_edit_property/{casaId}",
                    arguments = listOf(navArgument("casaId") { type = NavType.IntType; defaultValue = -1 })
                ) { backStackEntry ->
                    val casaId = backStackEntry.arguments?.getInt("casaId")
                    AddEditPropertyScreenVm(
                        casaId = if (casaId == -1) null else casaId,
                        onNavigateBack = onNavigateBack,
                        casasViewModel = casasViewModel
                    )
                }

                composable("admin_solicitudes") { AdminSolicitudesScreen(historialViewModel = historialViewModel) }

                // --- OTRAS RUTAS ---
                composable("edit_profile") { editarperfilScreen(perfilViewModel = perfilViewModel, onSaveChanges = onNavigateBack) }
                composable("change_password") { CambiarClaveScreen(authViewModel = authViewModel, onSaveChanges = onNavigateBack) }
            }
        }
    }
}