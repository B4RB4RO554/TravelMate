package tn.bidpaifusion.travelmatekotlin.data.api

import retrofit2.http.*

// Currency API Models
data class CurrencyConversionRequest(
    val from: String,
    val to: String,
    val amount: Double
)

data class CurrencyConversionResponse(
    val from: String,
    val to: String,
    val amount: Double,
    val converted: Double,
    val rate: Double
)

// Emergency API Models
data class PlaceSearchRequest(
    val lat: Double,
    val lon: Double,
    val type: String, // "hospital", "police", "fuel"
    val radius: Int = 5000
)

data class Place(
    val name: String,
    val address: String,
    val lat: Double,
    val lon: Double,
    val phone: String? = null,
    val distance: Double? = null
)

data class PlacesResponse(
    val places: List<Place>
)

data class EmergencyNumbers(
    val country: String,
    val police: String,
    val ambulance: String,
    val fire: String
)

// Distress API Models
data class DistressSignalRequest(
    val latitude: Double,
    val longitude: Double,
    val message: String
)

data class DistressSignalResponse(
    val _id: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val message: String,
    val timestamp: String,
    val isActive: Boolean
)

// Translation API Models
data class TranslationRequest(
    val text: String,
    val source: String,
    val target: String
)

data class TranslationResponse(
    val translated: String
)

interface EmergencyApiService {
    @GET("emergency/places")
    suspend fun searchPlaces(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("type") type: String
    ): List<Place>

    @GET("emergency/numbers")
    suspend fun getEmergencyNumbers(
        @Query("country") country: String
    ): EmergencyNumbers
}

interface CurrencyApiService {
    @GET("currency/convert")
    suspend fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): CurrencyConversionResponse
}

interface DistressApiService {
    @POST("distress")
    suspend fun sendDistressSignal(
        @Header("Authorization") token: String,
        @Body request: DistressSignalRequest
    ): DistressSignalResponse

    @GET("distress")
    suspend fun getActiveDistressSignals(
        @Header("Authorization") token: String
    ): List<DistressSignalResponse>

    @PUT("distress/{id}/deactivate")
    suspend fun deactivateDistressSignal(
        @Header("Authorization") token: String,
        @Path("id") signalId: String
    ): DistressSignalResponse
}

interface TranslationApiService {
    @POST("translate")
    suspend fun translate(
        @Body request: TranslationRequest
    ): TranslationResponse
}
