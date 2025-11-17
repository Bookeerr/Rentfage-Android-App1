package com.example.rentfage.data.repository

import com.example.rentfage.data.local.casasDeEjemplo
import com.example.rentfage.data.local.room.dao.CasaDao
import com.example.rentfage.data.local.room.entity.CasaEntity
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

    // --- LOGICA DE INICIALIZACION ---
    suspend fun popularBaseDeDatosSiEsNecesario() {
        if (casaDao.count() == 0) {
            val casasEntity = casasDeEjemplo.map { casa ->
                CasaEntity(
                    id = casa.id,
                    price = casa.price,
                    address = casa.address,
                    details = casa.details,
                    imageUri = casa.imageUri,
                    latitude = casa.latitude,
                    longitude = casa.longitude,
                    isFavorite = casa.isFavorite
                )
            }
            casaDao.insertarTodas(casasEntity)
        }
    }
}
