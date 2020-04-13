package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.App

class LocalDataSourceImpl :
    LocalDataSource {
    private val dao = WeatherDataBase.getInstance(
        App.instance
    ).weatherDataDao()
    override suspend fun getFavoritesPlaces(): List<PlaceEntity> {
        return dao.getFavoritesPlaces()
    }
    override suspend fun getCurrentLocation(): PlaceEntity? {
        val currentList = dao.getCurrentLocation()
        return if (currentList.isNotEmpty()){
            currentList[0]
        }else null
    }

    override suspend fun getSearchHistory(): List<PlaceEntity> {
       return dao.getSearchHistory()
    }

    override suspend fun saveCurrentPlace(placeEntity: CurrentTable) {
            dao.saveCurrentPlaceWithReplace(placeEntity)
            logD("saved  current place")


    }

    override suspend fun saveFavoritePlace(placeEntity: FavoriteTable) {
        dao.saveFavoritePlace(placeEntity)
        logD("saved  favorite place")
    }


    override suspend fun getLastRequest(): List<WeatherEntity> {
        val lastResponse = dao.getLastResponse()
        logD(lastResponse.toString())
        return dao.getLastResponse()
    }

    override suspend fun saveRequest(weatherEntity: WeatherEntity) {
        dao.saveResponse(weatherEntity)
    }

    override suspend fun getResponse(geoPoint: GeoPoint): WeatherEntity? {
        return dao.getResponse(geoPoint.pointToId())
    }

    override suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean {
        val isFavorite = dao.placeIsFavorite(placeEntity.toGeoPoint().pointToId())
        logD("placeIsFavorite ${isFavorite != null}")
        return isFavorite != null
    }

    override suspend fun removeFavoritePlace(placeEntity: PlaceEntity) {
        val favoriteDeleted =dao.removeFavoritePlace(placeEntity.toGeoPoint().pointToId())
        logD("removeFavoritePlace $favoriteDeleted")
    }

    override suspend fun savePlaceToHistory(placeEntity: PlaceEntity) {
        dao.savePlaceToHistoryMaxPlace(placeEntity.toSearchHistoryTable())
        logD("savePlaceToHistory ")
    }


}