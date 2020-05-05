package com.srgpanov.simpleweather.data.local


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse

class RoomConverter {


    @TypeConverter
    fun fromWeather(weatherResponse: OneCallResponse): String {
        return Gson().toJson(weatherResponse)


    }

    @TypeConverter
    fun toWeather(data: String): OneCallResponse {
        return Gson().fromJson(data, OneCallResponse::class.java)
    }

    @TypeConverter
    fun fromCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): String {
        return Gson().toJson(currentWeatherResponse)


    }

    @TypeConverter
    fun toCurrentWeather(data: String): CurrentWeatherResponse {
        return Gson().fromJson(data, CurrentWeatherResponse::class.java)
    }
}