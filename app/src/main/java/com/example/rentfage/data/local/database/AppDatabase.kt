package com.example.rentfage.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rentfage.data.local.dao.CasaDao
import com.example.rentfage.data.local.dao.UserDao
import com.example.rentfage.data.local.entity.CasaEntity
import com.example.rentfage.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, CasaEntity::class],
    version = 4, // Versión incrementada por el cambio en la tabla de usuarios
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun casaDao(): CasaDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "rentfage.db"

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                // Seed para Usuarios
                                val userDao = getInstance(context).userDao()
                                if (userDao.count() == 0) {
                                    // AQUÍ SE PRECARGAN LOS USUARIOS INICIALES
                                    val userSeed = listOf(
                                        UserEntity(name = "Admin", email = "a@a.cl", phone = "12345678", pass = "Admin123!", role = "ADMIN"),
                                        UserEntity(name = "victor rosendo", email = "b@b.cl", phone = "12345678", pass = "Jose123!") // Rol USER por defecto
                                    )
                                    userSeed.forEach { userDao.insertar(it) }
                                }

                                // Seed para Casas
                                val casaDao = getInstance(context).casaDao()
                                if (casaDao.count() == 0) {
                                    // AQUÍ SE PRECARGAN LAS CASAS INICIALES
                                    val casaSeed = listOf(
                                        CasaEntity(
                                            price = "$250.000 CLP",
                                            address = "Calle Falsa 123, Santiago",
                                            details = "Acogedor departamento de 2 ambientes, ideal para una persona o pareja. Cerca de comercios y transporte público.",
                                            imageUri = "android.resource://com.example.rentfage/drawable/casa1",
                                            latitude = -33.4489,
                                            longitude = -70.6693
                                        ),
                                        CasaEntity(
                                            price = "$450.000 CLP",
                                            address = "Avenida Siempreviva 742, Providencia",
                                            details = "Amplia casa de 3 dormitorios y 2 baños, con un gran patio trasero y estacionamiento. Perfecta para una familia.",
                                            imageUri = "android.resource://com.example.rentfage/drawable/casa2",
                                            latitude = -33.4314,
                                            longitude = -70.6083
                                        ),
                                        CasaEntity(
                                            price = "$320.000 CLP",
                                            address = "Pasaje Los Lirios 45, Las Condes",
                                            details = "Moderno loft con excelente iluminación natural. Edificio cuenta con gimnasio y piscina.",
                                            imageUri = "android.resource://com.example.rentfage/drawable/casa3",
                                            latitude = -33.4164,
                                            longitude = -70.5679
                                        )
                                    )
                                    casaDao.insertarTodas(casaSeed)
                                }
                            }
                        }
                    }).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}