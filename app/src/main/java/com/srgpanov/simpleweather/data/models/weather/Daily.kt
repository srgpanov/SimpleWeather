package com.srgpanov.simpleweather.data.models.weather


import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logE
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

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
        val time = (dt*1000L)-offsetFromUtc+(offset)
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
        val time = (sunrise*1000L)-offsetFromUtc+(offset)
        val calendar = Calendar.getInstance()
        calendar.time= Date(time)
        return calendar.get(HOUR_OF_DAY)
    }

    fun getHourSunset(): Int {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunset*1000L)-offsetFromUtc+(offset)
        val calendar = Calendar.getInstance()
        calendar.time=Date(time)
        return calendar.get(HOUR_OF_DAY)
    }

    fun getSunriseString(): String {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunrise*1000L)-offsetFromUtc+(offset)
        logD("getSunriseString ${ SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))}")
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
    }

    fun getSunsetString(): String {
        val tz = TimeZone.getDefault()
        val offsetFromUtc = tz.getOffset(System.currentTimeMillis())
        val time = (sunset*1000L)-offsetFromUtc+(offset)
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
    }

    fun dayLightHours(context: Context): String {
        val dayLightHours = (sunset - sunrise) * 1000L
        val calendar = Calendar.getInstance()
        calendar.time = Date(dayLightHours)
        val hours: String = if (calendar.get(HOUR_OF_DAY) < 10) {
            "0${calendar.get(HOUR_OF_DAY)}"
        } else
            calendar.get(HOUR_OF_DAY).toString()
        val minute =
            if (calendar.get(MINUTE) < 10) "0${calendar.get(MINUTE)}"
            else calendar.get(MINUTE).toString()
        val h = context.getString(R.string.hours_short)
        val min = context.getString(R.string.minutes_short)
        return "$hours $h $minute $min"
    }
    fun windDirection():String{
        return getWindDirection(windDeg)
    }
    fun windDirectionIcon():Int{
        return getWindDirectionIcon(windDeg)
    }
    fun weatherFormated():String{
        try {
        val builder = StringBuilder()
        weather.forEachIndexed { index, weather ->
            if (index!=this.weather.size-1){
                builder.append(weather.description).append(", ")
            }else{
                builder.append(weather.description)
            }
        }
        return firstLetterToUpperCase(builder.toString())
        }catch (e:IndexOutOfBoundsException){
            logE("weatherFormated $e")
            return ""
        }
    }

    private fun getHourString(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendarTime().time)
//        val calendar = Calendar.getInstance()
//        calendar.time = date
//        return "${calendar.get(HOUR_OF_DAY)}:${calendar.get(MINUTE)}"
    }

    private fun getHour(): Int {
        return calendarTime().get(HOUR_OF_DAY)
    }

}