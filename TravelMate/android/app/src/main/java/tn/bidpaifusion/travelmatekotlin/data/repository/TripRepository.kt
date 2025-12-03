package tn.bidpaifusion.travelmatekotlin.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.bidpaifusion.travelmatekotlin.data.local.AppDatabase
import tn.bidpaifusion.travelmatekotlin.data.local.entity.TripEntity
import tn.bidpaifusion.travelmatekotlin.data.local.entity.toEntity
import tn.bidpaifusion.travelmatekotlin.data.local.entity.toTrip
import tn.bidpaifusion.travelmatekotlin.data.local.entity.toTrips
import tn.bidpaifusion.travelmatekotlin.data.models.Trip
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Path

data class CreateTripRequest(
    val destination: String,
    val startDate: String,
    val endDate: String,
    val notes: String
)

interface TripService {
    @GET("/api/trips")
    suspend fun getTrips(@Header("Authorization") token: String): List<Trip>

    @POST("/api/trips")
    suspend fun createTrip(
        @Header("Authorization") token: String,
        @Body trip: CreateTripRequest
    ): Trip

    @DELETE("/api/trips/{id}")
    suspend fun deleteTrip(
        @Header("Authorization") token: String,
        @Path("id") tripId: String
    )
}

class TripRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val tripDao = database.tripDao()
    
    private val api = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TripService::class.java)

    // Offline-first: Return local data as Flow, sync in background
    fun getTripsFlow(userId: String): Flow<List<Trip>> {
        return tripDao.getTripsByUserId(userId).map { it.toTrips() }
    }

    // Fetch from API and update local database
    suspend fun syncTrips(token: String, userId: String): Result<Unit> {
        return try {
            val apiTrips = api.getTrips("Bearer $token")
            val tripEntities = apiTrips.map { it.toEntity(userId) }
            tripDao.insertTrips(tripEntities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get trips (try API first, fallback to local)
    suspend fun fetchTrips(token: String, userId: String): List<Trip> {
        return try {
            val apiTrips = api.getTrips("Bearer $token")
            // Cache to local database
            val tripEntities = apiTrips.map { it.toEntity(userId) }
            tripDao.insertTrips(tripEntities)
            apiTrips
        } catch (e: Exception) {
            // Fallback to local database if API fails
            val localTrips = tripDao.getTripsByUserId(userId)
            // Note: This returns a Flow, we need to collect it once
            // For now, get unsynced trips as fallback
            tripDao.getUnsyncedTrips().toTrips()
        }
    }

    // Create trip (save locally, sync when online)
    suspend fun createTrip(
        token: String?,
        userId: String,
        destination: String,
        startDate: String,
        endDate: String,
        notes: String
    ): Result<Trip> {
        return try {
            if (token != null) {
                // Try to create on server
                val request = CreateTripRequest(destination, startDate, endDate, notes)
                val trip = api.createTrip("Bearer $token", request)
                tripDao.insertTrip(trip.toEntity(userId))
                Result.success(trip)
            } else {
                // Offline mode: save locally with temp ID
                val tempId = "temp_${System.currentTimeMillis()}"
                val tripEntity = TripEntity(
                    id = tempId,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    userId = userId,
                    isSynced = false
                )
                tripDao.insertTrip(tripEntity)
                Result.success(tripEntity.toTrip())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete trip
    suspend fun deleteTrip(token: String?, tripId: String): Result<Unit> {
        return try {
            if (token != null) {
                api.deleteTrip("Bearer $token", tripId)
            }
            tripDao.deleteTripById(tripId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sync unsynced trips when back online
    suspend fun syncUnsyncedTrips(token: String): Result<Unit> {
        return try {
            val unsyncedTrips = tripDao.getUnsyncedTrips()
            for (trip in unsyncedTrips) {
                val request = CreateTripRequest(
                    destination = trip.destination,
                    startDate = trip.startDate,
                    endDate = trip.endDate,
                    notes = trip.notes
                )
                val serverTrip = api.createTrip("Bearer $token", request)
                // Delete temp trip and insert server trip
                tripDao.deleteTripById(trip.id)
                tripDao.insertTrip(serverTrip.toEntity(trip.userId))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
