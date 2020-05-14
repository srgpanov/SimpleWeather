package com.srgpanov.simpleweather.data.models.weather.current_weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.Weather
import com.srgpanov.simpleweather.other.logD
import kotlinx.android.parcel.Parcelize
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
@Parcelize
data class CurrentWeatherResponse(
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
    val timezone: Int,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind
) : Parcelable {
    fun localTime():String{

        val tz = TimeZone.getDefault()
        val now = Date()
        val offsetFromUtc = tz.getOffset(now.time)
        val time = System.currentTimeMillis()-offsetFromUtc+(timezone*1000L)
        logD("offsetFromUtc ${Date(time)} ${offsetFromUtc} ${Date(timezone*1000L)}")
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
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