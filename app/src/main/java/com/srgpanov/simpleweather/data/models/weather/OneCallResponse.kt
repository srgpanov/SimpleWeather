package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import kotlinx.android.parcel.Parcelize
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
    val timezone: String
) : Parcelable {


    fun setOffsets(): OneCallResponse {
        logD("WeatherResponse init")
        val timeZone = TimeZone.getTimeZone(timezone)
        hourly.forEach {
            it.offset = timeZone.rawOffset
        }
        daily.forEach {
            it.offset = timeZone.rawOffset
        }
        current.offset = timeZone.rawOffset
        return this
    }

    fun getGeoPoint(): GeoPoint {
        return GeoPoint(
            lat = lat,
            lon = lon
        )
    }
}

