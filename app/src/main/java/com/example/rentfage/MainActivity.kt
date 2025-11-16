package com.example.rentfage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.rentfage.navigation.AppNavGraph // <-- AHORA SÍ, EL IMPORT CORRECTO
import com.example.rentfage.ui.theme.RentfageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}


@Composable
fun AppRoot() {
    val navController = rememberNavController()
    RentfageTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Y aquí llamamos a la función con el nombre correcto
            AppNavGraph(navController = navController) 
        }
    }
}