package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.ip_to_location.IpToLocation
import com.srgpanov.simpleweather.data.models.places.Places
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.SimpleWeatherResponse
import com.srgpanov.simpleweather.other.numbersAfterDot
import java.util.*
import javax.inject.Inject


class RemoteDataSourceImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val placesService: PlacesService,
    private val ipToLocationService: IpToLocationService
) {


    suspend fun getOneCallWeather(
        lat: Double,
        lon: Double
    ): ResponseResult<OneCallResponse> {
        return weatherService.oneCallRequest(lat.numbersAfterDot(), lon.numbersAfterDot())
    }

    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): ResponseResult<SimpleWeatherResponse> {
        return weatherService.currentWeatherRequest(lat.numbersAfterDot(), lon.numbersAfterDot())
    }


    suspend fun getPlaces(query: String): ResponseResult<Places> {
        var lang = Locale.getDefault().toLanguageTag().replace("-", "_")
        lang = when (lang) {
            "ru_RU", "en_US" -> lang
            else -> "en_US"
        }
        return placesService.getPlaces(
            geocode = query,
            apikey = "57ac35b4-0384-4657-8171-fa6d8daff9e7",
            lang = lang
        )
    }

    suspend fun getGeoPointFromIp(): ResponseResult<IpToLocation> {
        return ipToLocationService.getLocation()

    }

}