package com.udistrital.parcial2ppc.domain.usecase

import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.domain.repository.TripRepository

class GetTripsUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(): Result<List<Trip>> {
        return repository.getTrips()
    }
}