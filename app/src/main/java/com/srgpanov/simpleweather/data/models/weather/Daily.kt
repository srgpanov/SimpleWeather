package com.srgpanov.simpleweather.data.models.weather


import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.other.format
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.Calendar.HOUR_OF_DAY

@Parcelize
data class Daily(
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("rain")
    val rain: Double,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("temp")
    val temp: Temp,
    @SerializedName("uvi")
    val uvi: Double,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
) : Parcelable {

    @IgnoredOnParcel
    var offset: Long = 0

    private fun calendarTime(): Calendar {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (dt * 1000L) - offsetFromUtc + (offset)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        return calendar
    }

    fun date(): Date {
        return calendarTime().time
    }

    fun getDay(): Int {
        return calendarTime().get(Calendar.DAY_OF_MONTH)
    }

    fun getHourSunrise(): Int {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunrise * 1000L) - offsetFromUtc + (offset)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        return calendar.get(HOUR_OF_DAY)
    }

    fun getHourSunset(): Int {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunset * 1000L) - offsetFromUtc + (offset)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        return calendar.get(HOUR_OF_DAY)
    }

    fun getSunriseString(): String {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunrise * 1000L) - offsetFromUtc + (offset)
        return Date(time).format("HH:mm")
    }

    fun getSunsetString(): String {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunset * 1000L) - offsetFromUtc + (offset)
        return Date(time).format("HH:mm")
    }

    fun dayLightHours(context: Context): String {
        val dayLightHours = (sunset - sunrise) * 1000L
        val date = Date(dayLightHours)
        val h = context.getString(R.string.hours_short)
        val min = context.getString(R.string.minutes_short)
        val hours = date.format("H")
        val minutes = date.format("mm")
        return "$hours $h $minutes $min"
    }

    fun windDirection(): String {
        return getWindDirection(windDeg)
    }

    fun windDirectionIcon(): Int {
        return getWindDirectionIcon(windDeg)
    }

    @SuppressLint("DefaultLocale")
    fun weatherFormatted(): String {
        val builder = StringBuilder()
        weather.forEachIndexed { index, weather ->
            if (index != this.weather.lastIndex) {
                builder.append(weather.description).append(", ")
            } else {
                builder.append(weather.description)
            }
        }
        return builder.toString().capitalize()

    }
}