package com.udistrital.parcial2ppc.data.repository

import com.udistrital.parcial2ppc.data.model.MiembroEntity
import com.udistrital.parcial2ppc.data.model.PlanDetail
import com.udistrital.parcial2ppc.data.model.PlanSummary
import com.udistrital.parcial2ppc.data.remote.ApiService

interface AhorroRepository {
    var lastError: String?
    suspend fun getPlanes(): List<PlanSummary>
    suspend fun getDetallePlan(id: Long): PlanDetail?
    suspend fun crearPlan(motivo: String, meta: Double, meses: Int, miembros: List<String>): Long
    suspend fun registrarPago(planId: Long, miembroId: Long, monto: Double): Boolean
    suspend fun getMiembrosForPlan(planId: Long): List<MiembroEntity>
}

class AhorroRepositoryImpl(
    private val apiService: ApiService
) : AhorroRepository {

    override var lastError: String? = null

    // In-memory fallback/cache to allow app usage even if local API is unreachable
    private val localPlanes = mutableListOf<PlanSummary>()
    private val localDetails = mutableMapOf<Long, PlanDetail>()
    private var nextPlanId = 1L

    override suspend fun getPlanes(): List<PlanSummary> {
        return try {
            val remote = apiService.getPlanes()
            lastError = null
            remote
        } catch (e: Exception) {
            lastError = "API no disponible. Mostrando datos locales."
            localPlanes.toList()
        }
    }

    override suspend fun getDetallePlan(id: Long): PlanDetail? {
        return try {
            val remote = apiService.getDetallePlan(id)
            lastError = null
            remote
        } catch (e: Exception) {
            lastError = "API no disponible. Mostrando datos locales."
            localDetails[id] ?: localPlanes.find { it.id == id }?.let {
                PlanDetail(
                    id = it.id,
                    motivo = it.motivo,
                    meta = it.meta,
                    totalAhorrado = it.totalAhorrado,
                    meses = it.meses,
                    miembros = emptyList()
                )
            }
        }
    }

    override suspend fun crearPlan(
        motivo: String,
        meta: Double,
        meses: Int,
        miembros: List<String>
    ): Long {
        val newId = nextPlanId++
        val newPlan = PlanSummary(
            id = newId,
            motivo = motivo,
            meta = meta,
            totalAhorrado = 0.0,
            progreso = 0f,
            meses = meses,
            totalMiembros = miembros.size
        )
        localPlanes.add(newPlan)
        localDetails[newId] = PlanDetail(
            id = newId,
            motivo = motivo,
            meta = meta,
            totalAhorrado = 0.0,
            meses = meses,
            miembros = miembros.mapIndexed { index, name ->
                MiembroEntity(id = (index + 1).toLong(), nombre = name, totalAportado = 0.0)
            }
        )

        return try {
            val req = mapOf(
                "motivo" to motivo,
                "meta" to meta,
                "meses" to meses,
                "miembros" to miembros
            )
            apiService.crearPlan(req)
            lastError = null
            newId
        } catch (e: Exception) {
            lastError = "Guardado localmente (servidor no disponible)"
            newId
        }
    }

    override suspend fun registrarPago(planId: Long, miembroId: Long, monto: Double): Boolean {
        var updated = false
        val detail = localDetails[planId]
        if (detail != null) {
            val updatedMembers = detail.miembros.map { m ->
                if (m.id == miembroId) m.copy(totalAportado = m.totalAportado + monto) else m
            }
            val newTotal = updatedMembers.sumOf { it.totalAportado }
            localDetails[planId] = detail.copy(
                totalAhorrado = newTotal,
                miembros = updatedMembers
            )

            val summaryIndex = localPlanes.indexOfFirst { it.id == planId }
            if (summaryIndex != -1) {
                val old = localPlanes[summaryIndex]
                val newProg = if (old.meta > 0) (newTotal / old.meta).toFloat() else 0f
                localPlanes[summaryIndex] = old.copy(totalAhorrado = newTotal, progreso = newProg)
            }
            updated = true
        }

        return try {
            apiService.registrarPago(planId, miembroId, monto)
            lastError = null
            true
        } catch (e: Exception) {
            if (updated) {
                lastError = "Pago registrado localmente (servidor no disponible)"
                true
            } else {
                lastError = e.localizedMessage
                false
            }
        }
    }

    override suspend fun getMiembrosForPlan(planId: Long): List<MiembroEntity> {
        return try {
            val remote = apiService.getMiembros(planId)
            lastError = null
            remote
        } catch (e: Exception) {
            localDetails[planId]?.miembros ?: emptyList()
        }
    }
}
