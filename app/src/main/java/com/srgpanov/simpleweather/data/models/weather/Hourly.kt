package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
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
    fun  day():Int{
        return calendarTime().get(Calendar.DAY_OF_MONTH)
    }
    fun  hour():Int{
        return calendarTime().get(Calendar.HOUR_OF_DAY)
    }

    fun tempFormatted(): String {
        return format(temp.roundToInt())
    }

    fun getDate(): String {
        val localTime = (dt * 1000L) - offset
        val date = Date(localTime)
        return SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }

    private fun calendarTime(): Calendar {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (dt*1000L)-offsetFromUtc+(offset*1000L)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        return calendar
    }

}