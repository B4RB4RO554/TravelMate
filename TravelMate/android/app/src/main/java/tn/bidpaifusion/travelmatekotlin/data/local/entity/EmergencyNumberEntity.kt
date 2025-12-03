package tn.bidpaifusion.travelmatekotlin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_numbers")
data class EmergencyNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val country: String,
    val police: String,
    val ambulance: String,
    val fire: String,
    val cachedAt: Long = System.currentTimeMillis()
)
