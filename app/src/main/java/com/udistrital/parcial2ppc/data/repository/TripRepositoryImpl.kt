package com.udistrital.parcial2ppc.data.repository

import com.udistrital.parcial2ppc.data.remote.TripApiService
import com.udistrital.parcial2ppc.data.remote.dto.CreateTripDTO
import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.domain.repository.TripRepository

class TripRepositoryImpl(
    private val api: TripApiService
) : TripRepository {

    override suspend fun getTrips(): Result<List<Trip>> {
        return try {
            val response = api.getTrips()
            val trips = response.map { dto ->
                Trip(
                    id = dto.id ?: "",
                    name = dto.name ?: "",
                    destination = dto.destination ?: "",
                    startDate = dto.startDate ?: "",
                    endDate = dto.endDate ?: ""
                )
            }
            Result.success(trips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripById(tripId: String): Result<Trip> {
        return try {
            val dto = api.getTripById(tripId)
            val trip = Trip(
                id = dto.id ?: "",
                name = dto.name ?: "",
                destination = dto.destination ?: "",
                startDate = dto.startDate ?: "",
                endDate = dto.endDate ?: ""
            )
            Result.success(trip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTrip(trip: Trip): Result<Unit> {
        return try {
            val dto = CreateTripDTO(
                name = trip.name,
                destination = trip.destination,
                startDate = trip.startDate,
                endDate = trip.endDate
            )
            api.createTrip(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}