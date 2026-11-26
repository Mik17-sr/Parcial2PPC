package com.udistrital.parcial2ppc.domain.usecase

import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.domain.repository.TripRepository

class CreateTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: Trip): Result<Unit> {
        if (trip.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Debes asignar un nombre al viaje"))
        }
        if (trip.destination.isBlank()) {
            return Result.failure(IllegalArgumentException("Debes asignar un destino al viaje"))
        }
        if (trip.startDate.isBlank() || trip.endDate.isBlank()) {
            return Result.failure(IllegalArgumentException("Debes elegir las fechas de viaje"))
        }
        if (trip.endDate < trip.startDate) {
            return Result.failure(IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial"))
        }
        return repository.createTrip(trip)
    }
}