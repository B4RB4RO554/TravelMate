package tn.bidpaifusion.travelmatekotlin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val token: String? = null,
    val lastSyncTime: Long = 0
)
