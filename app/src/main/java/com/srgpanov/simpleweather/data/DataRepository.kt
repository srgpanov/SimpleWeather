package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse

interface DataRepository {
    suspend fun getWeather(geoPoint: GeoPoint):WeatherResponse?
    suspend fun getFreshWeather(geoPoint: GeoPoint):WeatherResponse?
    suspend fun getSimpleWeather(geoPoint: GeoPoint): WeatherResponse?
    suspend fun getFavoritePlaces():List<PlaceEntity>

    suspend fun getCurrentPlace(): PlaceEntity?
    suspend fun saveWeatherResponse(weatherResponse: WeatherResponse)
    suspend fun saveCurrentPlace(placeEntity: PlaceEntity)
    suspend fun saveFavoritePlace(placeEntity: PlaceEntity)
    suspend fun placeIsFavorite(placeEntity: PlaceEntity):Boolean
    suspend fun placeIsCurrent(placeEntity: PlaceEntity):Boolean
    suspend fun removeFavoritePlace(placeEntity: PlaceEntity)
    suspend fun savePlaceToHistory(placeEntity: PlaceEntity)
    suspend fun getSearchHistory(): List<PlaceEntity>
    suspend fun getSimpleFreshWeather(geoPoint: GeoPoint):WeatherResponse?


}