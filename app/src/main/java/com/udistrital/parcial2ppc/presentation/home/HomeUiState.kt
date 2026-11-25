package com.udistrital.parcial2ppc.presentation.home

import com.udistrital.parcial2ppc.domain.model.Trip

data class HomeUiState(
    val isLoading: Boolean = false,
    val planes: List<Trip> = emptyList(),
    val errorMessage: String? = null
)
