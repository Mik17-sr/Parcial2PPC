package com.udistrital.parcial2ppc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.parcial2ppc.domain.usecase.GetTripsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.udistrital.parcial2ppc.data.remote.RetrofitClient
import com.udistrital.parcial2ppc.data.repository.TripRepositoryImpl

class HomeViewModel : ViewModel() {
    private val getTripsUseCase = GetTripsUseCase(TripRepositoryImpl(RetrofitClient.api))

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPlanes()
    }

    fun loadPlanes() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val planes = getTripsUseCase().getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    planes = planes,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }
}
