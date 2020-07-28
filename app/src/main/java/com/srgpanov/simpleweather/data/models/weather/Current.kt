package com.srgpanov.simpleweather.data.models.weather


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.other.logE
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.roundToInt

@Parcelize
data class Current(
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
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("uvi")
    val uvi: Double,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Float
) : Parcelable {
    @IgnoredOnParcel
    var offset:Long=0
    fun tempFormatted():String{
        val tempInt =temp.roundToInt()
        return format(tempInt)
    }
    fun feelsLikeFormatted():String{
        val tempInt =feelsLike.roundToInt()
        return format(tempInt)
    }
    fun getDate():Date{
        return Date(dt*1000L)
    }

    @SuppressLint("DefaultLocale")
    fun weatherFormatted():String{
        return try {
            val builder = StringBuilder()
            weather.forEachIndexed { index, weather ->
                if (index != this.weather.size - 1) {
                    builder.append(weather.description).append(", ")
                } else {
                    builder.append(weather.description)
                }
            }
            builder.toString().capitalize()
        } catch (e: IndexOutOfBoundsException) {
            logE("weatherFormated $e")
            ""
        }
    }
    fun windDirection():String{
        return getWindDirection(windDeg)
    }
    fun windDirectionIcon():Int{
        return getWindDirectionIcon(windDeg)
    }



}