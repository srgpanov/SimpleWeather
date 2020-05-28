package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.entity.utility.PlacesWithWeather
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor( val dao:WeatherDao) {

    suspend fun getFavoritesPlaces(): List<PlaceEntity> {
        val favoriteList = mutableListOf<PlaceEntity>()
        dao.getFavorites().mapTo(favoriteList) {
            it.toPlaceEntity()
        }
        return favoriteList
    }

    suspend fun getCurrentLocation(): PlaceEntity? {
        val places = mutableListOf<PlaceEntity>()
        val currentList = dao.getCurrentLocation().mapTo(places) {
            it.toPlaceEntity()
        }
        return if (currentList.isNotEmpty()) {
            currentList[0]
        } else null
    }

    suspend fun getSearchHistory(): List<PlaceEntity> {
        val placesHistory = mutableListOf<PlaceEntity>()
        dao.getSearchHistory().mapTo(placesHistory) {
            it.toPlaceEntity()
        }
        return placesHistory
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
        return dao.getOneCallResponse(
            geoPoint.pointToId().also { logD("getOneCallResponse id $it") })
    }

    suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean {
        val isFavorite = dao.placeIsFavorite(placeEntity.toGeoPoint().pointToId())
        logD("placeIsFavorite ${isFavorite != null}")
        return isFavorite != null
    }

    suspend fun removeFavoritePlace(placeEntity: PlaceEntity) {
        val favoriteDeleted = dao.removeFavoritePlace(placeEntity.toGeoPoint().pointToId())
        logD("removeFavoritePlace $favoriteDeleted")
    }

    suspend fun savePlaceToHistory(placeEntity: PlaceEntity) {
        dao.savePlaceToHistory(placeEntity.toSearchHistoryTable())
        logD("savePlaceToHistory ")
    }

    suspend fun getCurrentResponse(geoPoint: GeoPoint): SimpleWeatherTable? {
        return dao.getCurrentResponse(geoPoint.pointToId())
    }

    suspend fun saveCurrentResponse(response: SimpleWeatherTable) {
        dao.saveCurrentResponse(response)
    }

    suspend fun savePlace(placeEntity: PlaceEntity) {
        logD("savePlace ${placeEntity.toPlaceTable().id}")
        dao.insertPlace(placeEntity.toPlaceTable())


    }

    suspend fun updatePlace(placeEntity: PlaceEntity) {
        dao.insertOrUpdatePlace(placeEntity.toPlaceTable())
    }

    suspend fun placeIsInDb(shownGeoPoint: GeoPoint):Boolean {
        val place = dao.placeIsInDb(shownGeoPoint.pointToId())
        return if (place==null) false else true
    }

    suspend fun getPlace(id: String): PlacesWithWeather? {
        return dao.getPlaceById(id)
    }


}