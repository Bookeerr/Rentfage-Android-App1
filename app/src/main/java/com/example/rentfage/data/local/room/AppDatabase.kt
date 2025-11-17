package com.example.rentfage.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rentfage.data.local.room.dao.CasaDao
import com.example.rentfage.data.local.room.entity.CasaEntity

// Anotacion que define la base de datos, sus entidades (tablas) y la version.
@Database(entities = [CasaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Metodo abstracto para obtener el DAO de las casas.
    // Room se encargara de generar la implementacion.
    abstract fun casaDao(): CasaDao

    // Companion object para implementar el patron Singleton.
    // Esto asegura que solo exista una instancia de la base de datos en toda la app.
    companion object {
        // La anotacion @Volatile asegura que el valor de INSTANCE sea siempre el mas actualizado.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Funcion para obtener la instancia de la base de datos.
        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe una instancia, la devolvemos.
            // Si no, creamos una nueva de forma segura (synchronized).
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rentfage_database" // Nombre del archivo de la base de datos.
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
