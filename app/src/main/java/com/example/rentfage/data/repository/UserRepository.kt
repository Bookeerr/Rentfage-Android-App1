package com.example.rentfage.data.repository

import com.example.rentfage.data.local.dao.UserDao
import com.example.rentfage.data.local.entity.UserEntity
import java.lang.IllegalArgumentException

class UserRepository (
    private val userDao: UserDao
){
    //inicio sesion
    suspend fun login(email:String, pass: String): Result<UserEntity>{
        val user = userDao.getByEmail(email)
        return if(user != null && user.pass == pass){
            Result.success(user)
        }
        else{
            Result.failure(IllegalArgumentException("Datos Inválidos"))
        }
    }

    //registro
    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long>{
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("Correo en uso"))
        }
        else{
            val id = userDao.insertar(
                UserEntity(
                    // id se autogenera, no se pasa aquí
                    name = name,
                    email = email,
                    phone = phone,
                    pass = pass
                    // El rol por defecto es "USER", definido en la entidad
                )
            )
            return Result.success(id)
        }
    }

    // Obtener todos los usuarios para la pantalla de admin
    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAll()
    }

    // Funciones para que el admin gestione roles
    suspend fun promoteToAdmin(user: UserEntity) {
        if (user.role != "ADMIN") { // Solo actuar si no es ya admin
            val updatedUser = user.copy(role = "ADMIN")
            userDao.updateUser(updatedUser)
        }
    }

    suspend fun demoteToUser(user: UserEntity) {
        if (user.role != "USER") { // Solo actuar si no es ya user
            val updatedUser = user.copy(role = "USER")
            userDao.updateUser(updatedUser)
        }
    }
}
