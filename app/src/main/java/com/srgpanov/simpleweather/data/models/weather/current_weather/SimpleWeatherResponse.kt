package com.srgpanov.simpleweather.data.models.weather.current_weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.Weather
import com.srgpanov.simpleweather.other.format
import com.srgpanov.simpleweather.other.weatherIsFresh
import kotlinx.android.parcel.Parcelize
import java.math.RoundingMode
import java.util.*

@Parcelize
data class SimpleWeatherResponse(
    @SerializedName("base")
    val base: String,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("cod")
    val cod: Int,
    @SerializedName("coord")
    val coord: Coord,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String,
    @SerializedName("sys")
    val sys: Sys,
    @SerializedName("timezone")
    val timezone: Long,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind,
    var timeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    val isFresh: Boolean
        get() = weatherIsFresh(timeStamp)

    fun localTime(): String {

        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time)
        val time = System.currentTimeMillis() - offsetFromUtc + (timezone * 1000L)
        return Date(time).format("HH:mm")
    }

    fun getGeoPoint(): GeoPoint {
        val latRounded = coord.lat.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        val lonRounded = coord.lon.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        return GeoPoint(
            lat = latRounded,
            lon = lonRounded
        )
    }
}