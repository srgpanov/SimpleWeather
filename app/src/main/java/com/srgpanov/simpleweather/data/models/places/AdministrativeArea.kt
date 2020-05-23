package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class AdministrativeArea(
    @SerializedName("AdministrativeAreaName")
    val administrativeAreaName: String,
    @SerializedName("SubAdministrativeArea")
    val subAdministrativeArea: SubAdministrativeArea
)