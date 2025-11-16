package com.example.rentfage.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rentfage.data.local.room.entity.CasaEntity
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) para la tabla de casas.
@Dao
interface CasaDao {

    // --- LECTURA ---
    @Query("SELECT * FROM casas ORDER BY id ASC")
    fun obtenerTodas(): Flow<List<CasaEntity>>

    @Query("SELECT * FROM casas WHERE id = :id")
    fun getById(id: Int): Flow<CasaEntity?>

    @Query("SELECT * FROM casas WHERE isFavorite = 1 ORDER BY id ASC")
    fun getFavoritas(): Flow<List<CasaEntity>>

    @Query("SELECT COUNT(*) FROM casas")
    suspend fun count(): Int

    // --- ESCRITURA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(casa: CasaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(casas: List<CasaEntity>)

    @Update
    suspend fun actualizar(casa: CasaEntity)

    @Delete
    suspend fun borrar(casa: CasaEntity)

    @Query("DELETE FROM casas")
    suspend fun borrarTodas()
}
