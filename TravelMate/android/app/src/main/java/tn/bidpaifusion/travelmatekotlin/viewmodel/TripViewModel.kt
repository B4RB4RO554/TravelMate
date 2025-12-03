package tn.bidpaifusion.travelmatekotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.models.Trip
import tn.bidpaifusion.travelmatekotlin.data.repository.TripRepository
import tn.bidpaifusion.travelmatekotlin.data.repository.OfflineDataManager
import tn.bidpaifusion.travelmatekotlin.data.repository.DataResult
import tn.bidpaifusion.travelmatekotlin.utils.NetworkMonitor

sealed class TripState {
    object Loading : TripState()
    data class Success(
        val trips: List<Trip>, 
        val fromCache: Boolean = false
    ) : TripState()
    data class Error(val message: String, val cachedTrips: List<Trip>? = null) : TripState()
}

class TripViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = TripRepository(application.applicationContext)
    private val offlineManager = OfflineDataManager(application.applicationContext)
    private val networkMonitor = NetworkMonitor(application.applicationContext)
    
    private val _state = MutableStateFlow<TripState>(TripState.Loading)
    val state = _state.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus = _syncStatus.asStateFlow()
    
    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()
    
    init {
        // Monitor network status
        viewModelScope.launch {
            networkMonitor.networkStatus.collect { online ->
                _isOnline.value = online
            }
        }
    }

    // Load trips with offline-first approach
    fun loadTrips(token: String, userId: String = "") {
        viewModelScope.launch {
            offlineManager.getTrips(token).collect { result ->
                when (result) {
                    is DataResult.Loading -> {
                        _state.value = TripState.Loading
                    }
                    is DataResult.Success -> {
                        _state.value = TripState.Success(
                            trips = result.data,
                            fromCache = result.fromCache
                        )
                        if (result.fromCache) {
                            _syncStatus.value = "Showing cached data"
                        }
                    }
                    is DataResult.Error -> {
                        _state.value = TripState.Error(
                            message = result.message,
                            cachedTrips = result.cachedData
                        )
                    }
                }
            }
        }
    }

    // Load trips from local database (offline-first)
    fun loadTripsOffline(userId: String) {
        viewModelScope.launch {
            repo.getTripsFlow(userId).collect { trips ->
                _state.value = TripState.Success(trips, fromCache = true)
            }
        }
    }

    // Sync with server
    fun syncTrips(token: String, userId: String) {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            val result = repo.syncTrips(token, userId)
            result.onSuccess {
                _syncStatus.value = "Sync successful"
                // Also sync unsynced local trips
                repo.syncUnsyncedTrips(token)
            }.onFailure {
                _syncStatus.value = "Sync failed: ${it.message}"
            }
        }
    }

    // Create new trip (works offline)
    fun createTrip(
        token: String?,
        userId: String,
        destination: String,
        startDate: String,
        endDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            val trip = Trip(
                _id = "",
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                notes = notes
            )
            
            val result = offlineManager.createTrip(token ?: "", trip)
            when (result) {
                is DataResult.Success -> {
                    _syncStatus.value = if (result.fromCache) {
                        "Trip saved locally (will sync when online)"
                    } else {
                        "Trip created and synced"
                    }
                    // Refresh the list
                    loadTrips(token ?: "", userId)
                }
                is DataResult.Error -> {
                    _syncStatus.value = "Failed: ${result.message}"
                }
                else -> {}
            }
        }
    }

    // Delete trip
    fun deleteTrip(token: String?, tripId: String) {
        viewModelScope.launch {
            repo.deleteTrip(token, tripId)
                .onSuccess {
                    _syncStatus.value = "Trip deleted"
                }
                .onFailure {
                    _syncStatus.value = "Failed to delete trip: ${it.message}"
                }
        }
    }
    
    // Force sync pending changes
    fun syncPendingChanges(token: String) {
        viewModelScope.launch {
            if (_isOnline.value) {
                _syncStatus.value = "Syncing..."
                val synced = offlineManager.syncPendingTrips(token)
                _syncStatus.value = "$synced trips synced"
            } else {
                _syncStatus.value = "Cannot sync - you're offline"
            }
        }
    }
    
    fun clearSyncStatus() {
        _syncStatus.value = null
    }
}
