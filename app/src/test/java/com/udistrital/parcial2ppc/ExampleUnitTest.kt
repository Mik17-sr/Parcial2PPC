package com.udistrital.parcial2ppc
import org.junit.Test
import org.junit.Assert.*
import com.udistrital.parcial2ppc.domain.usecase.TravelValidation

class ExampleUnitTest {
    @Test
    fun tripRequiresNameAndDestination() {
        assertEquals(
            "El nombre del viaje es obligatorio",
            TravelValidation.validateTrip("", "Italia", "2026-06-10", "2026-06-20")
        )
        assertEquals(
            "El destino es obligatorio",
            TravelValidation.validateTrip("Europa", "", "2026-06-10", "2026-06-20")
        )
    }

    @Test
    fun tripEndDateCannotPrecedeStartDate() {
        assertEquals(
            "La fecha final no puede ser anterior a la fecha inicial",
            TravelValidation.validateTrip("Europa", "Italia", "2026-06-20", "2026-06-10")
        )
    }

    @Test
    fun placeDateMustBeInsideTripPeriod() {
        assertEquals(
            "La fecha del lugar debe estar dentro del periodo del viaje",
            TravelValidation.validatePlace(
                "Coliseo",
                "Anfiteatro romano",
                "Roma",
                1,
                "2026-06-21",
                "2026-06-10",
                "2026-06-20"
            )
        )
    }

    @Test
    fun validPlaceAndTripAreAccepted() {
        assertNull(
            TravelValidation.validateTrip("Europa", "Italia", "2026-06-10", "2026-06-20")
        )
        assertNull(
            TravelValidation.validatePlace(
                "Coliseo",
                "Anfiteatro romano",
                "Roma",
                1,
                "2026-06-12",
                "2026-06-10",
                "2026-06-20"
            )
        )
    }
}