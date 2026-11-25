package com.udistrital.parcial2ppc.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    private val BASE_URL = "http://localhost:3000"

    val api: TripApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripApiService::class.java)
    }
}