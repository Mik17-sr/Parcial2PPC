package com.udistrital.parcial2ppc.presentation.crear_viaje

data class CrearPlanUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
