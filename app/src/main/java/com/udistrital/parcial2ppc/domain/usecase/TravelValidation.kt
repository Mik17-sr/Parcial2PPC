package com.udistrital.parcial2ppc.domain.usecase

object TravelValidation {
    fun validateTrip(name: String, destination: String, startDate: String, endDate: String): String? {
        if (name.isBlank()) return "El nombre del viaje es obligatorio"
        if (destination.isBlank()) return "El destino es obligatorio"
        if (startDate.isBlank()) return "La fecha de inicio es obligatoria"
        if (endDate.isBlank()) return "La fecha de finalizacion es obligatoria"
        if (endDate < startDate) return "La fecha final no puede ser anterior a la fecha inicial"
        return null
    }

    fun validatePlace(
        name: String,
        description: String,
        city: String,
        categoryId: Int,
        tentativeDate: String,
        tripStartDate: String,
        tripEndDate: String
    ): String? {
        if (name.isBlank()) return "El nombre del lugar es obligatorio"
        if (description.isBlank()) return "La descripcion es obligatoria"
        if (city.isBlank()) return "La ciudad es obligatoria"
        if (categoryId <= 0) return "Seleccione una categoria"
        if (tentativeDate.isBlank()) return "La fecha tentativa es obligatoria"
        if (tentativeDate < tripStartDate || tentativeDate > tripEndDate) {
            return "La fecha del lugar debe estar dentro del periodo del viaje"
        }
        return null
    }
}
