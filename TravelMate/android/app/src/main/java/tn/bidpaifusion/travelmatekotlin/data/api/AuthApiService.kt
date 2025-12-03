package tn.bidpaifusion.travelmatekotlin.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import tn.bidpaifusion.travelmatekotlin.data.models.LoginRequest
import tn.bidpaifusion.travelmatekotlin.data.models.LoginResponse
import tn.bidpaifusion.travelmatekotlin.data.models.RegisterRequest

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}
