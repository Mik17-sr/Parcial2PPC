package com.udistrital.parcial2ppc.data.remote

import com.udistrital.parcial2ppc.data.remote.dto.CreateTripDTO
import com.udistrital.parcial2ppc.data.remote.dto.TripDTO
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

}