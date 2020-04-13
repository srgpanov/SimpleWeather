package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.CurrentTable
import com.srgpanov.simpleweather.data.models.entity.FavoriteTable
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.places.FeatureMember

interface LocalDataSource {
    suspend fun getFavoritesPlaces():List<PlaceEntity>
    suspend fun getCurrentLocation(): PlaceEntity?
    suspend fun getSearchHistory():List<PlaceEntity>
    suspend fun saveCurrentPlace(placeEntity: CurrentTable)


    suspend fun saveFavoritePlace(placeEntity: FavoriteTable)
    suspend fun getLastRequest():List<WeatherEntity>
    suspend fun saveRequest(weatherEntity: WeatherEntity)
    suspend fun getResponse(geoPoint: GeoPoint): WeatherEntity?
    suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean
    suspend fun removeFavoritePlace(placeEntity: PlaceEntity)
    suspend fun savePlaceToHistory(placeEntity: PlaceEntity)


}