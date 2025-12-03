package tn.bidpaifusion.travelmatekotlin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val notes: String,
    val userId: String,
    val isSynced: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)
