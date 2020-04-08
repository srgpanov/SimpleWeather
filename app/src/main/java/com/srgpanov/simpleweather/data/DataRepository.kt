package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse

interface DataRepository {
    suspend fun getWeather(geoPoint: GeoPoint):WeatherResponse?
    suspend fun getFreshWeather(geoPoint: GeoPoint):WeatherResponse?
    suspend fun getSimpleWeather(geoPoint: GeoPoint): WeatherResponse?
    suspend fun getFavoritePlaces():List<PlaceEntity>

    suspend fun getCurrentPlace(): PlaceEntity?
    suspend fun saveWeatherResponse(weatherResponse: WeatherResponse)
    suspend fun savePlace(placeEntity: PlaceEntity)
    suspend fun getPlace(geoPoint: GeoPoint): PlaceEntity?
    suspend fun changeFavotiteStatus(placeEntity: PlaceEntity)

}