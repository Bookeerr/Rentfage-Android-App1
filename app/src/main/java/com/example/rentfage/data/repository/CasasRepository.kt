package com.example.rentfage.data.repository

import com.example.rentfage.data.local.dao.CasaDao
import com.example.rentfage.data.local.entity.CasaEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para manejar los datos de las casas.
class CasasRepository(private val casaDao: CasaDao) {

    // --- LECTURA ---
    val todasLasCasas: Flow<List<CasaEntity>> = casaDao.obtenerTodas()
    val casasFavoritas: Flow<List<CasaEntity>> = casaDao.getFavoritas()

    fun getById(id: Int): Flow<CasaEntity?> {
        return casaDao.getById(id)
    }

    // --- ESCRITURA ---
    suspend fun insertarCasa(casa: CasaEntity) {
        casaDao.insertar(casa)
    }

    suspend fun actualizarCasa(casa: CasaEntity) {
        casaDao.actualizar(casa)
    }

    suspend fun borrarCasa(casa: CasaEntity) {
        casaDao.borrar(casa)
    }
}