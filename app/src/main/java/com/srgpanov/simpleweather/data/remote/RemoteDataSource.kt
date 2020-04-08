package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.places.Places
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse

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