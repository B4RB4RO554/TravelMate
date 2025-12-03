package tn.bidpaifusion.travelmatekotlin.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Weather API service for fetching weather data
 */
interface WeatherApiService {

    @GET("api/weather")
    suspend fun getWeather(
        @Query("city") city: String
    ): Response<WeatherResponse>

    @GET("api/weather/forecast")
    suspend fun getForecast(
        @Query("city") city: String
    ): Response<ForecastResponse>

    @GET("api/weather/alerts")
    suspend fun getWeatherAlerts(
        @Query("city") city: String
    ): Response<WeatherAlertsResponse>
}

// Response models
data class WeatherResponse(
    val city: String,
    val temperature: Int,
    val temperatureUnit: String,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int,
    val windUnit: String,
    val icon: String,
    val timestamp: String,
    val forecast: List<ForecastDay>?
)

data class ForecastResponse(
    val city: String,
    val forecast: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val dayName: String,
    val tempHigh: Int,
    val tempLow: Int,
    val condition: String,
    val icon: String,
    val precipitationChance: Int
)

data class WeatherAlertsResponse(
    val city: String,
    val alerts: List<WeatherAlert>,
    val timestamp: String
)

data class WeatherAlert(
    val type: String,
    val severity: String,
    val message: String,
    val icon: String
)
