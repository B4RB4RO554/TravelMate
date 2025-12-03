package tn.bidpaifusion.travelmatekotlin.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance
import tn.bidpaifusion.travelmatekotlin.data.local.AppDatabase
import tn.bidpaifusion.travelmatekotlin.data.local.entity.TripEntity
import tn.bidpaifusion.travelmatekotlin.data.local.entity.POIEntity
import tn.bidpaifusion.travelmatekotlin.data.local.entity.EmergencyNumberEntity
import tn.bidpaifusion.travelmatekotlin.data.models.Trip
import tn.bidpaifusion.travelmatekotlin.utils.NetworkMonitor

/**
 * Offline-first data manager that handles:
 * 1. Loading data from local Room database first
 * 2. Syncing with remote API when online
 * 3. Caching API responses locally
 */
class OfflineDataManager(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val tripDao = database.tripDao()
    private val poiDao = database.poiDao()
    private val emergencyDao = database.emergencyNumberDao()
    private val networkMonitor = NetworkMonitor(context)
    
    private val tripApi = RetrofitInstance.tripApi
    private val emergencyApi = RetrofitInstance.emergencyApi
    
    // ==================== TRIPS ====================
    
    /**
     * Get all trips - offline first approach
     * Returns cached data immediately, then syncs with server if online
     */
    fun getTrips(token: String): Flow<DataResult<List<Trip>>> = flow {
        // 1. Emit cached data first
        val cachedTrips = tripDao.getAllTrips().first()
        if (cachedTrips.isNotEmpty()) {
            emit(DataResult.Success(cachedTrips.map { it.toTrip() }, fromCache = true))
        } else {
            emit(DataResult.Loading)
        }
        
        // 2. Try to fetch from network if online
        if (networkMonitor.checkCurrentConnectivity()) {
            try {
                val response = tripApi.getTrips("Bearer $token")
                if (!response.isSuccessful || response.body() == null) {
                    throw Exception("Failed to fetch trips")
                }
                val remoteTrips = response.body()!!

                // Cache the results
                val entities = remoteTrips.map { trip ->
                    TripEntity(
                        id = trip._id,
                        destination = trip.destination,
                        startDate = trip.startDate,
                        endDate = trip.endDate,
                        notes = trip.notes ?: "",
                        userId = "",
                        isSynced = true
                    )
                }
                tripDao.insertTrips(entities)
                
                emit(DataResult.Success(remoteTrips, fromCache = false))
            } catch (e: Exception) {
                // Network failed, but we already emitted cache
                emit(DataResult.Error("Sync failed: ${e.message}", cachedTrips.map { it.toTrip() }))
            }
        } else {
            // Offline - just use cache
            if (cachedTrips.isEmpty()) {
                emit(DataResult.Error("No internet and no cached data", emptyList()))
            }
        }
    }
    
    /**
     * Create a trip - works offline
     */
    suspend fun createTrip(token: String, trip: Trip): DataResult<Trip> {
        // 1. Save locally first (always works)
        val entity = TripEntity(
            id = "local_${System.currentTimeMillis()}",
            destination = trip.destination,
            startDate = trip.startDate,
            endDate = trip.endDate,
            notes = trip.notes ?: "",
            userId = "",
            isSynced = false // Mark as not synced
        )
        tripDao.insertTrip(entity)
        
        // 2. Try to sync with server
        if (networkMonitor.checkCurrentConnectivity()) {
            return try {
                val response = tripApi.createTrip("Bearer $token", trip)
                if (!response.isSuccessful || response.body() == null) {
                    throw Exception("Failed to create trip")
                }
                val remoteTrip = response.body()!!
                // Update local with server ID
                tripDao.deleteTrip(entity)
                tripDao.insertTrip(entity.copy(id = remoteTrip._id, isSynced = true))
                DataResult.Success(remoteTrip, fromCache = false)
            } catch (e: Exception) {
                DataResult.Success(entity.toTrip(), fromCache = true) // Return local version
            }
        }
        
        return DataResult.Success(entity.toTrip(), fromCache = true)
    }
    
    /**
     * Sync all unsynced trips with server
     */
    suspend fun syncPendingTrips(token: String): Int {
        if (!networkMonitor.checkCurrentConnectivity()) return 0
        
        val unsyncedTrips = tripDao.getUnsyncedTrips()
        var syncedCount = 0
        
        for (trip in unsyncedTrips) {
            try {
                val response = tripApi.createTrip("Bearer $token", trip.toTrip())
                if (response.isSuccessful && response.body() != null) {
                    val remoteTrip = response.body()!!
                    tripDao.deleteTrip(trip)
                    tripDao.insertTrip(trip.copy(id = remoteTrip._id, isSynced = true))
                    syncedCount++
                }
            } catch (e: Exception) {
                // Skip this one, try next
            }
        }
        
        return syncedCount
    }
    
    // ==================== POI / PLACES ====================
    
    /**
     * Get nearby places - offline first
     */
    fun getPlaces(type: String, lat: Double, lon: Double): Flow<DataResult<List<POIEntity>>> = flow {
        // 1. Check cache first
        val range = 0.05 // ~5km radius
        val cached = poiDao.getPOIsInArea(type, lat - range, lat + range, lon - range, lon + range).first()
        
        if (cached.isNotEmpty()) {
            emit(DataResult.Success(cached, fromCache = true))
        } else {
            emit(DataResult.Loading)
        }
        
        // 2. Fetch from network if online
        if (networkMonitor.checkCurrentConnectivity()) {
            try {
                val remotePlaces = emergencyApi.searchPlaces(lat, lon, type)
                
                // Cache results
                val entities = remotePlaces.map { place ->
                    POIEntity(
                        name = place.name,
                        type = type,
                        address = place.address,
                        latitude = place.lat,
                        longitude = place.lon,
                        phone = place.phone
                    )
                }
                poiDao.insertPOIs(entities)
                
                emit(DataResult.Success(entities, fromCache = false))
            } catch (e: Exception) {
                if (cached.isEmpty()) {
                    emit(DataResult.Error("Failed to load places: ${e.message}", emptyList()))
                }
            }
        }
    }
    
    // ==================== EMERGENCY NUMBERS ====================
    
    /**
     * Get emergency numbers - offline first
     */
    suspend fun getEmergencyNumbers(countryCode: String): DataResult<EmergencyNumberEntity> {
        // 1. Check cache
        val cached = emergencyDao.getEmergencyNumbersByCountry(countryCode)
        
        if (cached != null && !networkMonitor.checkCurrentConnectivity()) {
            return DataResult.Success(cached, fromCache = true)
        }
        
        // 2. Try network
        if (networkMonitor.checkCurrentConnectivity()) {
            return try {
                val remote = emergencyApi.getEmergencyNumbers(countryCode)
                val entity = EmergencyNumberEntity(
                    country = countryCode,
                    police = remote.police,
                    ambulance = remote.ambulance,
                    fire = remote.fire
                )
                emergencyDao.insertEmergencyNumber(entity)
                DataResult.Success(entity, fromCache = false)
            } catch (e: Exception) {
                cached?.let { DataResult.Success(it, fromCache = true) }
                    ?: DataResult.Error("No data available", null)
            }
        }
        
        return cached?.let { DataResult.Success(it, fromCache = true) }
            ?: DataResult.Error("No internet and no cached data", null)
    }
    
    // ==================== UTILITIES ====================
    
    fun isOnline(): Boolean = networkMonitor.checkCurrentConnectivity()
    
    fun observeConnectivity(): Flow<Boolean> = networkMonitor.networkStatus
}

/**
 * Generic result wrapper for offline-first operations
 */
sealed class DataResult<out T> {
    object Loading : DataResult<Nothing>()
    data class Success<T>(val data: T, val fromCache: Boolean) : DataResult<T>()
    data class Error<T>(val message: String, val cachedData: T?) : DataResult<T>()
}

// Extension to convert entity to domain model
private fun TripEntity.toTrip() = Trip(
    _id = id,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    notes = notes
)
