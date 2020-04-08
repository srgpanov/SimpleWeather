package com.srgpanov.simpleweather.data.models.weather

import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

data class WeatherResponse(
    val fact: Fact,
    @SerializedName("forecasts")
    val forecasts: List<Forecast>,
    val info: Info,
    val now: Long,
    val now_dt: String
){
    fun getServerHour(): Int {
        var hour = this.now_dt.split("T")[1].take(2).toInt()
        hour += (this.info.tzinfo.offset / 60 / 60)
        return when (hour < 24) {
            true -> {
                when (hour < 0) {
                    true -> hour + 24
                    false -> hour
                }
            }
            false -> hour - 24
        }
    }
    fun getGeoPoint():GeoPoint{
        return GeoPoint(
            lat = info.lat,
            lon = info.lon
        )
    }
    fun getLocalTime():String{
        val calendar =Calendar.getInstance()
        val localTime =(now+info.tzinfo.offset)*1000
        calendar.time = Date(localTime)
        val simpleDateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
        simpleDateFormat.calendar=calendar

        return simpleDateFormat.format(calendar.time)
    }
}