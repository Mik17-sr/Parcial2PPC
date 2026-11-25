package com.udistrital.parcial2ppc.presentation.home

import com.udistrital.parcial2ppc.data.model.PlanSummary

data class HomeUiState(
    val isLoading: Boolean = false,
    val planes: List<PlanSummary> = emptyList(),
    val errorMessage: String? = null
)
