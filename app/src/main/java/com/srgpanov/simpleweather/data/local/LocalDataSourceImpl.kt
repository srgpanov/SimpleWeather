package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.CurrentEntity
import com.srgpanov.simpleweather.data.models.entity.FavoriteEntity
import com.srgpanov.simpleweather.data.models.entity.OneCallEntity
import com.srgpanov.simpleweather.data.models.entity.SimpleWeatherEntity
import com.srgpanov.simpleweather.data.models.entity.query_entity.PlacesWithWeather
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val dao: WeatherDao) {

    fun getFavoritesPlaces(): Flow<List<PlaceViewItem>> {
        return dao
            .getFavorites()
            .map { place ->
                place.map { item ->
                    item.toPlaceEntity().copy(favorite = true)
                }
            }
            .flowOn(Dispatchers.IO)

    }

    fun getCurrentLocationFlow(): Flow<PlaceViewItem?> {
        return dao.getCurrentLocationFlow()
            .map {
                it?.toPlaceEntity()?.copy(current = true)
            }
    }

    suspend fun getCurrentLocation(): PlaceViewItem? {
        val list = dao.getCurrentLocation()
        check(list.size <= 1) { "DB have several current places" }
        val place: CurrentEntity = list.getOrNull(0) ?: return null
        val placesWithWeather = dao.getPlaceById(place.id)
        return placesWithWeather?.toPlaceEntity()?.copy(current = true)
    }


    suspend fun getSearchHistory(): List<PlaceViewItem> {
        val placesHistory = mutableListOf<PlaceViewItem>()
        dao.getSearchHistory().mapTo(placesHistory) {
            it.toPlaceEntity()
        }
        return placesHistory
    }

    suspend fun saveCurrentPlace(placeEntity: CurrentEntity) {
        dao.saveCurrentPlaceWithReplace(placeEntity)
        logD("saved  current place")
    }

    suspend fun saveFavoritePlace(placeEntity: FavoriteEntity) {
        dao.saveFavoritePlace(placeEntity)
        logD("saved  favorite place")
    }


    suspend fun getLastRequest(): List<OneCallEntity> {
        val lastResponse = dao.getLastResponse()
        logD(lastResponse.toString())
        return lastResponse
    }

    suspend fun saveOneCallResponse(weatherEntity: OneCallEntity) {
        val weather = weatherEntity.oneCallResponse
        weather.timeStamp = System.currentTimeMillis()
        logD("saveOneCallResponse response saved")
        dao.saveOneCallResponse(weatherEntity.copy(oneCallResponse = weather))
    }

    suspend fun getOneCallResponse(geoPoint: GeoPoint): OneCallEntity? {
        return dao.getOneCallResponse(
            geoPoint.pointToId().also { logD("getOneCallResponse id $it") })
    }

    fun getOneCallResponseFlow(geoPoint: GeoPoint): Flow<OneCallEntity?> {
        return dao.getOneCallResponseFlow(geoPoint.pointToId())
    }


    suspend fun placeIsFavorite(placeViewItem: PlaceViewItem): Boolean {
        val isFavorite = dao.placeIsFavorite(placeViewItem.toGeoPoint().pointToId())
        logD("placeIsFavorite ${isFavorite != null}")
        return isFavorite != null
    }

    suspend fun removeFavoritePlace(placeViewItem: PlaceViewItem) {
        val favoriteDeleted = dao.removeFavoritePlace(placeViewItem.toGeoPoint().pointToId())
        logD("removeFavoritePlace $favoriteDeleted")
    }

    suspend fun savePlaceToHistory(placeViewItem: PlaceViewItem) {
        dao.savePlaceToHistory(placeViewItem.toSearchHistoryTable())
        logD("savePlaceToHistory ")
    }

    suspend fun getCurrentResponse(geoPoint: GeoPoint): SimpleWeatherEntity? {
        return dao.getCurrentResponse(geoPoint.pointToId())
    }

    suspend fun saveCurrentResponse(response: SimpleWeatherEntity) {
        val weather = response.simpleWeatherResponse
        weather.timeStamp = System.currentTimeMillis()
        dao.saveCurrentResponse(response.copy(simpleWeatherResponse = weather))
    }

    suspend fun savePlace(placeViewItem: PlaceViewItem) {
        logD("savePlace ${placeViewItem.toPlaceTable().id}")
        dao.insertPlace(placeViewItem.toPlaceTable())
    }

    suspend fun updatePlace(placeViewItem: PlaceViewItem) {
        dao.insertOrUpdatePlace(placeViewItem.toPlaceTable())
    }

    suspend fun placeIsInDb(shownGeoPoint: GeoPoint): Boolean {
        val place = dao.placeIsInDb(shownGeoPoint.pointToId())
        return place != null
    }

    suspend fun getPlace(id: String): PlacesWithWeather? {
        return dao.getPlaceById(id)
    }

    fun getPlaceFlow(id: String): Flow<PlacesWithWeather> {
        return dao.getPlaceByIdFlow(id)
    }

    fun placeIsFavoriteFlow(id: String): Flow<FavoriteEntity?> {
        return dao.placeIsFavoriteFlow(id)
    }


}