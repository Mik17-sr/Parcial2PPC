package com.udistrital.parcial2ppc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateTripDTO (
    @SerializedName("name") val name: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)