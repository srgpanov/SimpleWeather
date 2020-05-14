package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.App

class LocalDataSourceImpl  {
    private val dao = WeatherDataBase.getInstance(
        App.instance
    ).weatherDataDao()
suspend fun getFavoritesPlaces(): List<PlaceEntity> {
        return dao.getFavoritesPlaces()
    }
     suspend fun getCurrentLocation(): PlaceEntity? {
        val currentList = dao.getCurrentLocation()
        return if (currentList.isNotEmpty()){
            currentList[0]
        }else null
    }

     suspend fun getSearchHistory(): List<PlaceEntity> {
       return dao.getSearchHistory()
    }

     suspend fun saveCurrentPlace(placeEntity: CurrentTable) {
            dao.saveCurrentPlaceWithReplace(placeEntity)
            logD("saved  current place")


    }

     suspend fun saveFavoritePlace(placeEntity: FavoriteTable) {
        dao.saveFavoritePlace(placeEntity)
        logD("saved  favorite place")
    }


     suspend fun getLastRequest(): List<OneCallTable> {
        val lastResponse = dao.getLastResponse()
        logD(lastResponse.toString())
        return lastResponse
    }

     suspend fun saveOneCallResponse(weatherEntity: OneCallTable) {
         logD("saveOneCallResponse response saved")
        dao.saveOneCallResponse(weatherEntity)
    }

     suspend fun getOneCallResponse(geoPoint: GeoPoint): OneCallTable? {
        return dao.getOneCallResponse(geoPoint.pointToId().also { logD("getOneCallResponse id $it") })
    }

     suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean {
        val isFavorite = dao.placeIsFavorite(placeEntity.toGeoPoint().pointToId())
        logD("placeIsFavorite ${isFavorite != null}")
        return isFavorite != null
    }

     suspend fun removeFavoritePlace(placeEntity: PlaceEntity) {
        val favoriteDeleted =dao.removeFavoritePlace(placeEntity.toGeoPoint().pointToId())
        logD("removeFavoritePlace $favoriteDeleted")
    }

     suspend fun savePlaceToHistory(placeEntity: PlaceEntity) {
        dao.savePlaceToHistoryMaxPlace(placeEntity.toSearchHistoryTable())
        logD("savePlaceToHistory ")
    }

    suspend fun getCurrentResponse(geoPoint: GeoPoint): SimpleWeatherTable? {
        return dao.getCurrentResponse(geoPoint.pointToId())
    }

    suspend fun saveCurrentResponse(response: SimpleWeatherTable) {
        dao.saveCurrentResponse(response)
    }


}