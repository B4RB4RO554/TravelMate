package tn.bidpaifusion.travelmatekotlin.data.models

data class Trip(
    val _id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val notes: String
)
