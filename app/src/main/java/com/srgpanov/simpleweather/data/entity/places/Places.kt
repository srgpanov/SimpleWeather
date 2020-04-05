package com.srgpanov.simpleweather.data.entity.places

import com.google.gson.annotations.SerializedName

data class Places(

    @SerializedName("response")
    val response: PlacesResponse
)