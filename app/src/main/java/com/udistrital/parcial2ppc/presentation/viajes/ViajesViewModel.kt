package com.udistrital.parcial2ppc.presentation.viajes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.parcial2ppc.data.remote.RetrofitClient
import com.udistrital.parcial2ppc.data.repository.CategoryRepositoryImpl
import com.udistrital.parcial2ppc.data.repository.PlaceRepositoryImpl
import com.udistrital.parcial2ppc.data.repository.TripRepositoryImpl
import com.udistrital.parcial2ppc.domain.model.Category
import com.udistrital.parcial2ppc.domain.model.Place
import com.udistrital.parcial2ppc.domain.model.Trip
import com.udistrital.parcial2ppc.domain.usecase.TravelValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ViajesUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedTrip: Trip? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ViajesViewModel : ViewModel() {
    private val tripRepository = TripRepositoryImpl(RetrofitClient.api)
    private val placeRepository = PlaceRepositoryImpl(RetrofitClient.api)
    private val categoryRepository = CategoryRepositoryImpl(RetrofitClient.api)

    private val _uiState = MutableStateFlow(ViajesUiState(isLoading = true))
    val uiState: StateFlow<ViajesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val tripsResult = tripRepository.getTrips()
            val categoriesResult = categoryRepository.getCategories()
            if (tripsResult.isFailure || categoriesResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = tripsResult.exceptionOrNull()?.message
                        ?: categoriesResult.exceptionOrNull()?.message
                        ?: "No fue posible conectar con la API"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    trips = tripsResult.getOrThrow(),
                    categories = categoriesResult.getOrThrow()
                )
            }
        }
    }

    fun openTrip(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = tripRepository.getTripById(tripId)
            result.fold(
                onSuccess = { trip ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedTrip = trip
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No fue posible consultar el viaje"
                    )
                }
            )
        }
    }

    fun closeTrip() {
        _uiState.value = _uiState.value.copy(selectedTrip = null)
    }

    fun createTrip(name: String, destination: String, startDate: String, endDate: String) {
        TravelValidation.validateTrip(name, destination, startDate, endDate)?.let { error ->
            _uiState.value = _uiState.value.copy(errorMessage = error)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = tripRepository.createTrip(
                Trip(name = name.trim(), destination = destination.trim(), startDate = startDate, endDate = endDate)
            )
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "Viaje creado correctamente")
                    refresh()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No fue posible crear el viaje"
                    )
                }
            )
        }
    }

    fun createPlace(name: String, description: String, city: String, categoryId: Int, tentativeDate: String) {
        val trip = _uiState.value.selectedTrip
        if (trip == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Seleccione un viaje")
            return
        }

        TravelValidation.validatePlace(
            name, description, city, categoryId, tentativeDate,
            trip.startDate, trip.endDate
        )?.let { error ->
            _uiState.value = _uiState.value.copy(errorMessage = error)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = placeRepository.createPlace(
                Place(
                    tripId = trip.id,
                    name = name.trim(),
                    description = description.trim(),
                    city = city.trim(),
                    categoryId = categoryId,
                    tentativeDate = tentativeDate
                )
            )
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(successMessage = "Lugar agregado correctamente")
                    openTrip(trip.id)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No fue posible crear el lugar"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
