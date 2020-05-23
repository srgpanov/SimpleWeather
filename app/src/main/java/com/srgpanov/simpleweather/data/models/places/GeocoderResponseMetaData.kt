package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class GeocoderResponseMetaData(
    @SerializedName("boundedBy")
    val boundedBy: BoundedBy,
    @SerializedName("found")
    val found: String,
    @SerializedName("Point")
    val point: Point,
    @SerializedName("request")
    val request: String,
    @SerializedName("results")
    val results: String
)