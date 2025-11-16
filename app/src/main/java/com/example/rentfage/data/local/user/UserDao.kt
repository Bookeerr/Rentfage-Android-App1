package com.example.rentfage.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    //insertar datos en la tabla
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(user: UserEntity): Long

    //obtener todos los datos de un usuario mediante su correo
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): UserEntity?

    //obtener los datos de todos los usuarios ordenados por id de manera ascendente
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

    //obtener la cantidad de registros de la tabla
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

}
