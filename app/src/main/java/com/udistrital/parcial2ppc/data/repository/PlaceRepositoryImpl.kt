package com.udistrital.parcial2ppc.data.repository

import com.udistrital.parcial2ppc.data.remote.TripApiService
import com.udistrital.parcial2ppc.data.remote.dto.CreatePlaceDTO
import com.udistrital.parcial2ppc.domain.model.Place
import com.udistrital.parcial2ppc.domain.repository.PlaceRepository

class PlaceRepositoryImpl(private val api: TripApiService) : PlaceRepository {
    override suspend fun getPlaces(tripId: String): Result<List<Place>> {
        return try {
            Result.success(api.getPlaces(tripId).map { dto ->
                Place(
                    id = dto.id ?: "",
                    tripId = dto.tripId ?: tripId,
                    name = dto.name ?: "",
                    description = dto.description ?: "",
                    city = dto.city ?: "",
                    categoryId = dto.categoryId ?: 0,
                    tentativeDate = dto.tentativeDate ?: ""
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlaceById(placeId: String): Result<Place> =
        Result.failure(UnsupportedOperationException("No hay endpoint para consultar un lugar por id"))

    override suspend fun createPlace(place: Place): Result<Place> {
        return try {
            val dto = api.createPlace(
                CreatePlaceDTO(
                    tripId = place.tripId,
                    name = place.name,
                    description = place.description,
                    city = place.city,
                    categoryId = place.categoryId,
                    tentativeDate = place.tentativeDate
                )
            )
            Result.success(
                Place(
                    id = dto.id ?: "",
                    tripId = dto.tripId ?: place.tripId,
                    name = dto.name ?: place.name,
                    description = dto.description ?: place.description,
                    city = dto.city ?: place.city,
                    categoryId = dto.categoryId ?: place.categoryId,
                    tentativeDate = dto.tentativeDate ?: place.tentativeDate
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlace(place: Place): Result<Unit> =
        Result.failure(UnsupportedOperationException("No hay endpoint para actualizar lugares"))
}
