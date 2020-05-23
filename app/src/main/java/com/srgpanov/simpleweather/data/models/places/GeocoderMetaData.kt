package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class GeocoderMetaData(
    @SerializedName("Address")
    val address: Address,
    @SerializedName("AddressDetails")
    val addressDetails: AddressDetails,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("precision")
    val precision: String,
    @SerializedName("text")
    val text: String
)