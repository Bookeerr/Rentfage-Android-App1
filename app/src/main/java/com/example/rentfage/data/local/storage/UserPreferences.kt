package com.example.rentfage.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear la instancia de DataStore
val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    // Llave para guardar el valor booleano
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    //Llave para guardar el rol del usuario (ej: "Admin" o "User").
    private val userRoleKey = stringPreferencesKey("user_role")

    // Funcion para escribir el estado de la sesión
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    // Funcion para guardar el rol del usuario cuando inicia sesión.
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[userRoleKey] = role
        }
    }

    //Función para limpiar el rol al cerrar sesión.
    suspend fun clearUserRole() {
        context.dataStore.edit { prefs ->
            prefs.remove(userRoleKey)
        }
    }

    // Flow para leer el estado de la sesión en tiempo real
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            // Si el valor no existe, devuelve 'false' por defecto
            prefs[isLoggedInKey] ?: false
        }

    // Flow para leer el rol del usuario en tiempo real.
    val userRole: Flow<String?> = context.dataStore.data
        .map { prefs ->
            // Devuelve el rol guardado, o null si no hay ninguno.
            prefs[userRoleKey]
        }
}
