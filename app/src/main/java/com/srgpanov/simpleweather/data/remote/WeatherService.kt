package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall?appid=ee66731ef4cb0a2a14721aa82373abaf&units=metric")
    suspend fun oneCallRequest(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double
    ): ResponseResult<OneCallResponse>

    @GET("weather?appid=ee66731ef4cb0a2a14721aa82373abaf&units=metric")
    suspend fun currentWeatherRequest(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double
    ): ResponseResult<CurrentWeatherResponse>



}

