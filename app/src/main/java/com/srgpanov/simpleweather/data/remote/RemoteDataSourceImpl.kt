package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.ip_to_location.IpToLocation
import com.srgpanov.simpleweather.data.models.places.Places
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse
import com.srgpanov.simpleweather.other.numbersAfterDot


class RemoteDataSourceImpl {
    private val weatherService =
        RetrofitClient.createWeatherService()
    private val placesService =
        RetrofitClient.createPlacesService()
    private val ipToLocationService =
        RetrofitClient.createIpToLocationService()

    suspend fun getOneCallWeather(
        lat: Double,
        lon: Double
    ): ResponseResult<OneCallResponse> {
        return weatherService.oneCallRequest(lat.numbersAfterDot(), lon.numbersAfterDot())
    }

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): ResponseResult<CurrentWeatherResponse> {
        return weatherService.currentWeatherRequest(lat.numbersAfterDot(), lon.numbersAfterDot())
    }


    suspend fun getPlaces(query: String, lang: String = "ru_RU"): ResponseResult<Places> {
        return placesService.getPlaces(
            geocode = query,
            apikey = "57ac35b4-0384-4657-8171-fa6d8daff9e7"
        )
    }

    suspend fun getGeoPointFromIp(): ResponseResult<IpToLocation> {
        return ipToLocationService.getLocation()

    }

}