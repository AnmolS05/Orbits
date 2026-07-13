package com.orbits.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OrbitEntity::class], version = 1, exportSchema = false)
abstract class OrbitDatabase : RoomDatabase() {
    abstract fun orbitDao(): OrbitDao

    companion object {
        @Volatile
        private var INSTANCE: OrbitDatabase? = null

        fun getDatabase(context: Context): OrbitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrbitDatabase::class.java,
                    "orbits_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
