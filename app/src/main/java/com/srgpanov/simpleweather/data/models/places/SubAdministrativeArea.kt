package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class SubAdministrativeArea(
    @SerializedName("Locality")
    val locality: Locality,
    @SerializedName("SubAdministrativeAreaName")
    val subAdministrativeAreaName: String
)