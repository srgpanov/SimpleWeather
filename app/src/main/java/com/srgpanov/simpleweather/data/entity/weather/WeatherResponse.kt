package com.srgpanov.simpleweather.data.entity.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val fact: Fact,
    @SerializedName("forecasts")
    val forecasts: List<Forecast>,
    val info: Info,
    val now: Int,
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
}