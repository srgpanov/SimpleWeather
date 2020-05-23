package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class GeoObject(
    @SerializedName("boundedBy")
    val boundedBy: BoundedBy,
    @SerializedName("description")
    val description: String,
    @SerializedName("metaDataProperty")
    val metaDataProperty: MetaDataProperty,
    @SerializedName("name")
    val name: String,
    @SerializedName("Point")
    val point: Point
)