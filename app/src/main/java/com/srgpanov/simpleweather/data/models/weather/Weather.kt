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
            "04n" -> R.drawable.ic_ovc //todomake icons
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
}