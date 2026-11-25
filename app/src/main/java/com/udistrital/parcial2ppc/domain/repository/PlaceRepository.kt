package com.udistrital.parcial2ppc.domain.repository

interface PlaceRepository {
    suspend fun getPlaces(): Result<List<SavingPlan>>
    suspend fun (planId: String): Result<SavingPlan>
    suspend fun createPlan(plan: SavingPlan): Result<SavingPlan>
    suspend fun registerPayment(planId: String, memberId: String, amount: Double): Result<Unit>
}