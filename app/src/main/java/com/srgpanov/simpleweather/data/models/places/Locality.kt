package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class Locality(
    @SerializedName("DependentLocality")
    val dependentLocality: DependentLocality,
    @SerializedName("LocalityName")
    val localityName: String
)