package com.udistrital.parcial2ppc.data.remote

import com.udistrital.parcial2ppc.data.remote.dto.CreateTripDTO
import com.udistrital.parcial2ppc.data.remote.dto.TripDTO
import com.udistrital.parcial2ppc.data.remote.dto.PlaceDTO
import com.udistrital.parcial2ppc.data.remote.dto.CreatePlaceDTO
import com.udistrital.parcial2ppc.data.remote.dto.CategoryDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TripApiService {

    @GET("api/trips")
    suspend fun getTrips(): List<TripDTO>

    @GET("api/trips/{trip_id}")
    suspend fun getTripById(@Path("trip_id") tripId: String): TripDTO

    @POST("api/trips")
    suspend fun createTrip(@Body request: CreateTripDTO): TripDTO

    @GET("api/places")
    suspend fun getPlaces(@retrofit2.http.Query("tripId") tripId: String): List<PlaceDTO>

    @POST("api/places")
    suspend fun createPlace(@Body request: CreatePlaceDTO): PlaceDTO

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDTO>

}