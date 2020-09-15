package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.SimpleWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface WeatherService {
    @GET("onecall?appid=ee66731ef4cb0a2a14721aa82373abaf&units=metric")
    suspend fun oneCallRequest(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("lang")
        lang:String=Locale.getDefault().language
    ): ResponseResult<OneCallResponse>

    @GET("weather?appid=ee66731ef4cb0a2a14721aa82373abaf&units=metric")
    suspend fun currentWeatherRequest(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("lang")
        lang: String = Locale.getDefault().language
    ): ResponseResult<SimpleWeatherResponse>



}

