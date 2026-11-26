package com.udistrital.parcial2ppc.domain.model

data class Place (
    val id: String = "",
    val tripId: String = "",
    val name: String = "",
    val description: String = "",
    val city: String = "",
    val categoryId: Int = 0,
    val tentativeDate: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)