package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class Envelope(
    @SerializedName("lowerCorner")
    val lowerCorner: String,
    @SerializedName("upperCorner")
    val upperCorner: String
)