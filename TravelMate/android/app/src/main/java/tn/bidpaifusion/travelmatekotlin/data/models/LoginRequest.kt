package tn.bidpaifusion.travelmatekotlin.data.models

data class LoginRequest(
    val emailOrUsername: String,
    val password: String
)
