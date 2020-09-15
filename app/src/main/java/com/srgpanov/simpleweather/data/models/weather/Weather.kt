package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String
) : Parcelable {
    fun getWeatherIcon(): Int {
        return when (icon) {
            "01d" -> R.drawable.ic_skc_d
            "02d" -> R.drawable.ic_bkn_d
            "03d" -> R.drawable.ic_ovc
            "04d" -> R.drawable.ic_ovc //todo make icons
            "09d" -> R.drawable.ic_ovc_ra
            "10d" -> R.drawable.ic_ovc__ra
            "11d" -> R.drawable.ic_ovc_ts_ra
            "13d" -> R.drawable.ic_ovc__sn
            "50d" -> R.drawable.ic_fg_d

            "01n" -> R.drawable.ic_skc_n
            "02n" -> R.drawable.ic_bkn_n
            "03n" -> R.drawable.ic_ovc
            "04n" -> R.drawable.ic_ovc //todoMake icons
            "09n" -> R.drawable.ic_bkn_ra_n
            "10n" -> R.drawable.ic_bkn__ra_n
            "11n" -> R.drawable.ic_ovc_ts_ra
            "13n" -> R.drawable.ic_bkn__sn_n
            "50n" -> R.drawable.ic_fg_d
            else -> {
                Log.e("TAG  ", "Cant find image")
                R.drawable.ic_ovc
            }
        }
    }
    fun getWeatherBackground(): Int {
        return when (icon) {
//            "01d" -> R.drawable.clear_sky_background//clear sky
//            "02d" -> R.drawable.few_clouds//few clouds
//            "03d" -> R.drawable.scattered_clouds//scattered clouds
//            "04d" -> R.drawable.broken_clouds //todo make icons  //broken clouds
//            "09d" -> R.drawable.shower_rain  //shower rain
//            "10d" -> R.drawable.rain //rain
//            "11d" -> R.drawable.thunderstorm//thunderstorm
//            "13d" -> R.drawable.snow //snow
//            "50d" -> R.drawable.mist //mist

            "01d" -> R.drawable.clear_sky_day//clear sky
            "02d" -> R.drawable.few_clouds_day//few clouds
            "03d" -> R.drawable.few_clouds_day//scattered clouds
            "04d" -> R.drawable.few_clouds_day  //broken clouds
            "09d" -> R.drawable.shower_rain_day  //shower rain
            "10d" -> R.drawable.rain_day //rain
            "11d" -> R.drawable.thunderstorm_day//thunderstorm
            "13d" -> R.drawable.snow_day //snow
            "50d" -> R.drawable.mist_day //mist
//
            "01n" -> R.drawable.clear_sky_night
            "02n" -> R.drawable.clear_sky_night
            "03n" -> R.drawable.clear_sky_night
            "04n" -> R.drawable.clear_sky_night //todo make icons
            "09n" -> R.drawable.clear_sky_night
            "10n" -> R.drawable.clear_sky_night
            "11n" -> R.drawable.clear_sky_night
            "13n" -> R.drawable.clear_sky_night
            "50n" -> R.drawable.clear_sky_night
            else -> {
                Log.e("TAG  ", "Cant find image")
                R.drawable.empty_weather_background
            }
        }
    }
}