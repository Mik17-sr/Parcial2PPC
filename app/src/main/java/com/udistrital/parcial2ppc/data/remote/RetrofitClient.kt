package com.udistrital.parcial2ppc.data.remote

import com.udistrital.parcial2ppc.data.model.MiembroEntity
import com.udistrital.parcial2ppc.data.model.PlanDetail
import com.udistrital.parcial2ppc.data.model.PlanSummary
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("planes")
    suspend fun getPlanes(): List<PlanSummary>

    @GET("planes/{id}")
    suspend fun getDetallePlan(@Path("id") id: Long): PlanDetail

    @POST("planes")
    suspend fun crearPlan(@Body request: Map<String, Any>): Map<String, Any>

    @POST("planes/{id}/pago")
    suspend fun registrarPago(
        @Path("id") planId: Long,
        @Query("miembroId") miembroId: Long,
        @Query("monto") monto: Double
    ): Map<String, Any>

    @GET("planes/{id}/miembros")
    suspend fun getMiembros(@Path("id") planId: Long): List<MiembroEntity>
}

object RetrofitClient {
    var baseUrl: String = "http://10.0.2.2:8080/api/"
        private set

    private var retrofit: Retrofit = buildRetrofit()

    val api: ApiService
        get() = retrofit.create(ApiService::class.java)

    fun updateBaseUrl(newUrl: String) {
        val formattedUrl = if (!newUrl.endsWith("/")) "$newUrl/" else newUrl
        baseUrl = formattedUrl
        retrofit = buildRetrofit()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
