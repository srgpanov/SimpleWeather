package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)