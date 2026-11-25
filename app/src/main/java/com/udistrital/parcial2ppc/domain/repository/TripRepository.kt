package com.udistrital.parcial2ppc.domain.repository

import com.udistrital.parcial2ppc.domain.model.Trip

interface TripRepository {
    suspend fun getTrips(): Result<List<Trip>>
    suspend fun getTripById(tripId: String): Result<Trip>
    suspend fun createTrip(trip: Trip): Result<Unit>
}