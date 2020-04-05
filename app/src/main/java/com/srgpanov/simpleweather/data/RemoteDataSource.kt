package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.entity.places.Places
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse

interface RemoteDataSource {
    suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String = "ru_RU",
        day: Int = 7,
        hours: Boolean = true,
        extra: Boolean = true
    ): WeatherResponse?

    suspend fun getPlaces(query: String,lang: String="ru_RU"):Places?
}