package com.udistrital.parcial2ppc.domain.repository

import com.udistrital.parcial2ppc.domain.model.Place

interface PlaceRepository {
    suspend fun getPlaces(tripId: String): Result<List<Place>>
    suspend fun getPlaceById(placeId: String): Result<Place>
    suspend fun createPlace(place: Place): Result<Place>
    suspend fun updatePlace(place: Place): Result<Unit>
}