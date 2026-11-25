package com.udistrital.parcial2ppc.data.model

data class PlanSummary(
    val id: Long = 0,
    val motivo: String = "",
    val meta: Double = 0.0,
    val totalAhorrado: Double = 0.0,
    val progreso: Float = 0f,
    val meses: Int = 0,
    val totalMiembros: Int = 0
)

data class PlanDetail(
    val id: Long = 0,
    val motivo: String = "",
    val meta: Double = 0.0,
    val totalAhorrado: Double = 0.0,
    val meses: Int = 0,
    val miembros: List<MiembroEntity> = emptyList()
)

data class MiembroEntity(
    val id: Long = 0,
    val nombre: String = "",
    val totalAportado: Double = 0.0
)
