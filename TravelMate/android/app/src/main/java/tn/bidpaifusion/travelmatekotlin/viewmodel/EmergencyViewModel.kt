package tn.bidpaifusion.travelmatekotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.api.Place
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance
import tn.bidpaifusion.travelmatekotlin.data.local.AppDatabase
import tn.bidpaifusion.travelmatekotlin.data.local.entity.POIEntity

data class EmergencyState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(),
    val selectedType: String = "hospital",
    val error: String? = null,
    val userLat: Double? = null,
    val userLon: Double? = null,
    val emergencyNumbers: Map<String, String> = emptyMap()
)

class EmergencyViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitInstance.emergencyApi
    private val database = AppDatabase.getDatabase(application)
    private val poiDao = database.poiDao()
    private val emergencyDao = database.emergencyNumberDao()

    private val _state = MutableStateFlow(EmergencyState())
    val state = _state.asStateFlow()

    fun updateLocation(lat: Double, lon: Double) {
        _state.value = _state.value.copy(userLat = lat, userLon = lon)
    }

    fun selectPlaceType(type: String) {
        _state.value = _state.value.copy(selectedType = type)
    }

    fun searchPlaces(token: String, type: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = api.searchPlaces(lat, lon, type)
                
                // Cache to local database
                val poiEntities = response.map { place ->
                    POIEntity(
                        name = place.name,
                        type = type,
                        address = place.address,
                        latitude = place.lat,
                        longitude = place.lon,
                        phone = place.phone
                    )
                }
                poiDao.insertPOIs(poiEntities)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    places = response,
                    selectedType = type
                )
            } catch (e: Exception) {
                // Try to load from cache
                loadPlacesFromCache(type, lat, lon)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Using cached data: ${e.message}"
                )
            }
        }
    }

    private suspend fun loadPlacesFromCache(type: String, lat: Double, lon: Double) {
        // Load nearby places from cache (simplified area calculation)
        val range = 0.05 // ~5km
        poiDao.getPOIsInArea(
            type = type,
            minLat = lat - range,
            maxLat = lat + range,
            minLon = lon - range,
            maxLon = lon + range
        ).collect { cached ->
            val places = cached.map { poi ->
                Place(
                    name = poi.name,
                    address = poi.address,
                    lat = poi.latitude,
                    lon = poi.longitude,
                    phone = poi.phone
                )
            }
            _state.value = _state.value.copy(places = places)
        }
    }

    fun loadEmergencyNumbers(country: String) {
        viewModelScope.launch {
            try {
                val numbers = api.getEmergencyNumbers(country)
                
                // Cache to database
                val entity = tn.bidpaifusion.travelmatekotlin.data.local.entity.EmergencyNumberEntity(
                    country = numbers.country,
                    police = numbers.police,
                    ambulance = numbers.ambulance,
                    fire = numbers.fire
                )
                emergencyDao.insertEmergencyNumber(entity)
                
                _state.value = _state.value.copy(
                    emergencyNumbers = mapOf(
                        "Police" to numbers.police,
                        "Ambulance" to numbers.ambulance,
                        "Fire" to numbers.fire
                    )
                )
            } catch (e: Exception) {
                // Load from cache
                val cached = emergencyDao.getEmergencyNumbersByCountry(country)
                if (cached != null) {
                    _state.value = _state.value.copy(
                        emergencyNumbers = mapOf(
                            "Police" to cached.police,
                            "Ambulance" to cached.ambulance,
                            "Fire" to cached.fire
                        )
                    )
                }
            }
        }
    }
}
