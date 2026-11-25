package com.udistrital.parcial2ppc.domain.usecase

import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.domain.repository.TripRepository

class GetTripById(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String): Result<Trip> {
        if (tripId.isBlank()) {
            return Result.failure(IllegalArgumentException("Debe elegir el id del viaje"))
        }

        return repository.getTripById(tripId)
    }
}