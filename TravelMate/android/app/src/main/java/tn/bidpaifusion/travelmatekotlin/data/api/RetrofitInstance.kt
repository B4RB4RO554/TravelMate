package tn.bidpaifusion.travelmatekotlin.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.bidpaifusion.travelmatekotlin.BuildConfig

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val emergencyApi: EmergencyApiService by lazy {
        retrofit.create(EmergencyApiService::class.java)
    }

    val currencyApi: CurrencyApiService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }

    val distressApi: DistressApiService by lazy {
        retrofit.create(DistressApiService::class.java)
    }

    val translationApi: TranslationApiService by lazy {
        retrofit.create(TranslationApiService::class.java)
    }

    val tripApi: TripApiService by lazy {
        retrofit.create(TripApiService::class.java)
    }

    // Legacy compatibility
    val api: AuthApiService by lazy {
        authApi
    }
}
