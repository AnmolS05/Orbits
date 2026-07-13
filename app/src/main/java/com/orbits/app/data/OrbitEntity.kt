package com.orbits.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orbits")
data class OrbitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val cleanUrl: String,
    val tags: String? = null,
    val notes: String? = null,
    val status: String = "Active",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null
)
