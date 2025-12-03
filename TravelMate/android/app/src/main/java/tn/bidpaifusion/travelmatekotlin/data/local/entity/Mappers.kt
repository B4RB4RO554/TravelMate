package tn.bidpaifusion.travelmatekotlin.data.local.entity

import tn.bidpaifusion.travelmatekotlin.data.models.Trip

// Extension functions to map between API models and Room entities

fun Trip.toEntity(userId: String): TripEntity {
    return TripEntity(
        id = this._id,
        destination = this.destination,
        startDate = this.startDate,
        endDate = this.endDate,
        notes = this.notes,
        userId = userId,
        isSynced = true,
        lastModified = System.currentTimeMillis()
    )
}

fun TripEntity.toTrip(): Trip {
    return Trip(
        _id = this.id,
        destination = this.destination,
        startDate = this.startDate,
        endDate = this.endDate,
        notes = this.notes
    )
}

fun List<TripEntity>.toTrips(): List<Trip> {
    return this.map { it.toTrip() }
}
