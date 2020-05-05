package com.srgpanov.simpleweather.data.models.weather.current_weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clouds(
    @SerializedName("all")
    val all: Int
) : Parcelable