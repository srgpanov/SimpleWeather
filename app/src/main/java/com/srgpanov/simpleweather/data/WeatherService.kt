package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {



    @GET("forecast/")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String = "ru_RU",
        @Query("limit") day: Int = 7,
        @Query("hours") hours: Boolean = true,
        @Query("extra") extra: Boolean = true
    ): Response<WeatherResponse>


}

