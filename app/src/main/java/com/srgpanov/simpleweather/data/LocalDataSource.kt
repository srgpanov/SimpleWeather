package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.entity.PlaceEntity

interface LocalDataSource {
    suspend fun getFavoritesPlaces():List<PlaceEntity>
    suspend fun getCurrentLocation():List<PlaceEntity>
    suspend fun savePlace(placeEntity: PlaceEntity)


//    suspend fun getLastRequest():List<WeatherEntity>
//    suspend fun saveRequest(weatherEntity: WeatherEntity)

}