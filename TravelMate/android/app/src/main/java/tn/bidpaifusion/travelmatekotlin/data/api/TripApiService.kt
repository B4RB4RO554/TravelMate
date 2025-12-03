package tn.bidpaifusion.travelmatekotlin.data.api

import retrofit2.Response
import retrofit2.http.*
import tn.bidpaifusion.travelmatekotlin.data.models.Trip

interface TripApiService {

    @GET("/api/trips")
    suspend fun getTrips(
        @Header("Authorization") token: String
    ): Response<List<Trip>>

    @GET("/api/trips/{id}")
    suspend fun getTripById(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<Trip>

    @POST("/api/trips")
    suspend fun createTrip(
        @Header("Authorization") token: String,
        @Body trip: Trip
    ): Response<Trip>

    @PUT("/api/trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body trip: Trip
    ): Response<Trip>

    @DELETE("/api/trips/{id}")
    suspend fun deleteTrip(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<Map<String, String>>

    @GET("/api/trips/shared")
    suspend fun getSharedTrips(
        @Header("Authorization") token: String
    ): Response<List<Trip>>

    @POST("/api/trips/{id}/share")
    suspend fun shareTrip(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Map<String, String>>
}
