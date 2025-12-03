package tn.bidpaifusion.travelmatekotlin.data.repository

import retrofit2.Response
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance
import tn.bidpaifusion.travelmatekotlin.data.models.LoginRequest
import tn.bidpaifusion.travelmatekotlin.data.models.LoginResponse
import tn.bidpaifusion.travelmatekotlin.data.models.RegisterRequest

class AuthRepository {

    private val api = RetrofitInstance.api // ✅ Use your shared Retrofit instance

    suspend fun loginUser(request: LoginRequest): Response<LoginResponse> {
        return api.login(request)
    }

    suspend fun registerUser(request: RegisterRequest): Response<Unit> {
        return api.register(request)
    }
}
