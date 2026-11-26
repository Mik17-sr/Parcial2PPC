package com.udistrital.parcial2ppc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceDTO(
    @SerializedName("id") val id: String? = null,
    @SerializedName("tripId") val tripId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("tentativeDate") val tentativeDate: String? = null,
    @SerializedName("category") val category: CategoryDTO? = null
)
