package tn.bidpaifusion.travelmatekotlin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class POIEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "restaurant", "hotel", "attraction", "hospital", "police", "fuel"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val rating: Float? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)
