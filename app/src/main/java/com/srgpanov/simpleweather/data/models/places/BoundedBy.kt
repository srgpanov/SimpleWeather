package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class BoundedBy(
    @SerializedName("Envelope")
    val envelope: Envelope
)