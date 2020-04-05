package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.entity.places.Places
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.other.ResponseResult
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logE
import java.io.IOException

class RemoteDataSourceImpl:RemoteDataSource {
    private val weatherService = RetrofitClient.createWeatherService()
    private val placesService = RetrofitClient.createPlacesService()
    suspend fun getWeather(): ResponseResult<out Any> {
        try {
            val response = weatherService.getWeather(45.035470, 38.975313)
            return (if (response.isSuccessful) {
                ResponseResult.Success(response.body())
            } else {
                ResponseResult.Error(
                    IOException("Error on loading weather"),
                    response.code()
                )
            })
        } catch (e: Exception) {
            return ResponseResult.Error(e)
        }
    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        lang: String,
        day: Int,
        hours: Boolean,
        extra: Boolean
    ): WeatherResponse? {
        try {
            val response =weatherService
                .getWeather(lat, lon, lang, day, hours, extra)
            return when (response.isSuccessful){
                true -> response.body()
                false -> null
            }
        }catch (e:Exception){
            logE("response error $e")
            return null
        }


    }
    override suspend fun getPlaces(query: String, lang: String): Places? {
        val response = placesService.getPlaces(
            geocode= query,
            apikey ="57ac35b4-0384-4657-8171-fa6d8daff9e7"
        )
        return when (response.isSuccessful) {
            true -> response.body()
            false -> null
        }
    }
}