package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import kotlinx.android.parcel.Parcelize
import java.math.RoundingMode
import java.util.*

@Parcelize
data class OneCallResponse(
    @SerializedName("current")
    val current: Current,
    @SerializedName("daily")
    val daily: List<Daily>,
    @SerializedName("hourly")
    val hourly: List<Hourly>,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezone_offset: Long
) : Parcelable {


    fun setOffsets(): OneCallResponse {
        logD("WeatherResponse init")
        logD("setOffsets ${Date(timezone_offset)}")
        hourly.forEach {
            it.offset = timezone_offset.toInt()
        }
        daily.forEach {
            it.offset = timezone_offset*1000
        }
        current.offset = timezone_offset*1000
        return this
    }


    fun getGeoPoint(): GeoPoint {
        val latRounded = lat.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        val lonRounded = lon.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        return GeoPoint(
            lat = latRounded,
            lon = lonRounded
        )
    }
}

