package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.other.logE
import kotlinx.android.parcel.Parcelize
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder
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

    var offset: Int = 0

    fun date(): Date {
        return Date(dt * 1000L + offset)
    }

    fun getDay(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date()
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getHourSunrise(): Int {
        val date = Date(sunrise * 1000L + offset)
        return getHour(date)
    }

    fun getHourSunset(): Int {
        val date = Date(sunset * 1000L + offset)
        return getHour(date)
    }

    fun getSunriseString(): String {
        val date = Date(sunrise * 1000L + offset)
        return getHourString(date)
    }

    fun getSunsetString(): String {
        val date = Date(sunset * 1000L + offset)
        return getHourString(date)
    }

    fun dayLightHours(): String {
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
        return "$hours h $minute min"
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

    private fun getHourString(date: Date): String {
        return SimpleDateFormat("HH:mm").format(date)
//        val calendar = Calendar.getInstance()
//        calendar.time = date
//        return "${calendar.get(HOUR_OF_DAY)}:${calendar.get(MINUTE)}"
    }

    private fun getHour(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(HOUR_OF_DAY)
    }

}