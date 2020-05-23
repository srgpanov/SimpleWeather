package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class MetaDataPropertyX(
    @SerializedName("GeocoderResponseMetaData")
    val geocoderResponseMetaData: GeocoderResponseMetaData
)