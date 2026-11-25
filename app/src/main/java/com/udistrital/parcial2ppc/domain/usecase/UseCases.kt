package com.udistrital.parcial2ppc.domain.usecase

import com.udistrital.parcial2ppc.data.model.PlanDetail
import com.udistrital.parcial2ppc.data.model.PlanSummary
import com.udistrital.parcial2ppc.data.repository.AhorroRepository

class GetPlanesUseCase(private val repository: AhorroRepository) {
    suspend operator fun invoke(): List<PlanSummary> = repository.getPlanes()
}

class CrearPlanUseCase(private val repository: AhorroRepository) {
    suspend operator fun invoke(
        motivo: String,
        meta: Double,
        meses: Int,
        miembros: List<String>
    ): Long = repository.crearPlan(motivo, meta, meses, miembros)
}

class GetDetallePlanUseCase(private val repository: AhorroRepository) {
    suspend operator fun invoke(id: Long): PlanDetail? = repository.getDetallePlan(id)
}

class RegistrarPagoUseCase(private val repository: AhorroRepository) {
    suspend operator fun invoke(planId: Long, miembroId: Long, monto: Double): Boolean =
        repository.registrarPago(planId, miembroId, monto)
}
