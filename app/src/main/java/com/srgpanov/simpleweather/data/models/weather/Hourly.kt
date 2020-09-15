package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.roundToInt

@Parcelize
data class Hourly(
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
) : Parcelable {
    @IgnoredOnParcel
    var offset: Int = 0

    val localTime: Long
        get() = (dt * 1000L) + offset

    fun day(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(localTime)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun hour(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(localTime)
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun tempFormatted(): String {
        return formatTemp(temp.roundToInt())
    }
}