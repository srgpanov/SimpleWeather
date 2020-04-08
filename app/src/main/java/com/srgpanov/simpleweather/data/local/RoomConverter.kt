package com.srgpanov.simpleweather.data.local


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse

class RoomConverter {


    @TypeConverter
    fun fromWeather(weatherResponse: WeatherResponse): String {
        return Gson().toJson(weatherResponse)


    }

    @TypeConverter
    fun toWeather(data: String): WeatherResponse {
        return Gson().fromJson(data, WeatherResponse::class.java)
    }
}