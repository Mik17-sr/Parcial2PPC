package com.udistrital.parcial2ppc.presentation.crear_viaje

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.parcial2ppc.data.repository.TripRepositoryImpl
import com.udistrital.parcial2ppc.domain.usecase.CreateTripUseCase
import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.utils.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.udistrital.parcial2ppc.data.remote.RetrofitClient

class CrearPlanViewModel : ViewModel() {
    private val repository = TripRepositoryImpl(RetrofitClient.api)
    private val crearPlanUseCase = CreateTripUseCase(repository)
    private val _uiState = MutableStateFlow(CrearPlanUiState())
    val uiState: StateFlow<CrearPlanUiState> = _uiState.asStateFlow()

    fun crearPlan(
        motivo: String,
        metaStr: String,
        mesesStr: String,
        miembrosStr: String,
        onSuccess: () -> Unit
    ) {
        if (motivo.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingrese el motivo del plan")
            return
        }
        val cleanMeta = CurrencyUtils.cleanMoneyInput(metaStr)
        if (cleanMeta <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingrese una meta válida")
            return
        }
        val meses = mesesStr.toIntOrNull()
        if (meses == null || meses <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingrese una cantidad de meses válida")
            return
        }
        val miembrosList = miembrosStr.split(",", "\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (miembrosList.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Por favor ingrese al menos un miembro")
            return
        }

        val trip = Trip(
            name = motivo,
            destination = "Destino desconocido", // placeholder o agregar a los parámetros
            startDate = "2026-01-01", // placeholder
            endDate = "2026-12-31" // placeholder
        )

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = crearPlanUseCase(trip)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar el plan en la API"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }
}
