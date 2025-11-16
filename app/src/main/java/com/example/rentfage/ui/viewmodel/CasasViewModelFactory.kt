package com.example.rentfage.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rentfage.data.repository.CasasRepository

// Esta clase es un "constructor especial" para nuestro CasasViewModel.
// Su unica mision es saber como crear un CasasViewModel pasandole el repositorio que necesita.
class CasasViewModelFactory(private val repository: CasasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprueba si la clase que se le pide crear es CasasViewModel.
        if (modelClass.isAssignableFrom(CasasViewModel::class.java)) {
            // Si lo es, crea la instancia con el repositorio y la devuelve.
            @Suppress("UNCHECKED_CAST")
            return CasasViewModel(repository) as T
        }
        // Si se le pide crear cualquier otro ViewModel, lanza un error.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
