package tn.bidpaifusion.travelmatekotlin.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import tn.bidpaifusion.travelmatekotlin.data.local.entity.TripEntity

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY startDate DESC")
    fun getTripsByUserId(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isSynced = 0")
    suspend fun getUnsyncedTrips(): List<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: String)

    @Query("UPDATE trips SET isSynced = 1 WHERE id = :tripId")
    suspend fun markAsSynced(tripId: String)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
}
