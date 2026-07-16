package com.orbits.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrbitDao {
    @Query("SELECT * FROM orbits ORDER BY isPinned DESC, createdAt DESC")
    fun getAllOrbits(): Flow<List<OrbitEntity>>

    @Query("SELECT * FROM orbits WHERE cleanUrl = :url LIMIT 1")
    suspend fun getByCleanUrl(url: String): OrbitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orbit: OrbitEntity): Long

    @Update
    suspend fun update(orbit: OrbitEntity)

    @Delete
    suspend fun delete(orbit: OrbitEntity)
}
