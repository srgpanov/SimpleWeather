package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class FeelsLike(
    @SerializedName("day")
    val day: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double,
    @SerializedName("night")
    val night: Double
) : Parcelable {
    fun dayFormatted(): String {
        val tempInt = day.roundToInt()
        return formatTemp(tempInt)
    }

    fun eveFormatted(): String {
        val tempInt = eve.roundToInt()
        return formatTemp(tempInt)
    }

    fun mornFormatted(): String {
        val tempInt = morn.roundToInt()
        return formatTemp(tempInt)
    }

    fun nightFormatted(): String {
        val tempInt = night.roundToInt()
        return formatTemp(tempInt)
    }
}