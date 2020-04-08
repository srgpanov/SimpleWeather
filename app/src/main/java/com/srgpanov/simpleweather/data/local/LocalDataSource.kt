package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint

interface LocalDataSource {
    suspend fun getFavoritesPlaces():List<PlaceEntity>
    suspend fun getCurrentLocation(): PlaceEntity?
    suspend fun savePlace(placeEntity: PlaceEntity)


    suspend fun getLastRequest():List<WeatherEntity>
    suspend fun saveRequest(weatherEntity: WeatherEntity)
    suspend fun getResponse(geoPoint: GeoPoint): WeatherEntity?
    suspend fun getPlace(geoPoint: GeoPoint): PlaceEntity?
    suspend fun changeFavoriteStatus(placeEntity: PlaceEntity)

}