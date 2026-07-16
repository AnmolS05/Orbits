package com.orbits.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [OrbitEntity::class], version = 2, exportSchema = false)
abstract class OrbitDatabase : RoomDatabase() {
    abstract fun orbitDao(): OrbitDao

    companion object {
        @Volatile
        private var INSTANCE: OrbitDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE orbits ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): OrbitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrbitDatabase::class.java,
                    "orbits_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
