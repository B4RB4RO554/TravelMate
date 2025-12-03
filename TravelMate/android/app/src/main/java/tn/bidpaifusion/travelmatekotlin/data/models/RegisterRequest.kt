package tn.bidpaifusion.travelmatekotlin.data.models

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val phone: String,
    val address: String,
    val profilePicture: String = "", // You can expand this later
    val username: String,
    val password: String
)
