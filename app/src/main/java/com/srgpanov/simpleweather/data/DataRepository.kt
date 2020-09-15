package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.entity.CurrentEntity
import com.srgpanov.simpleweather.data.models.entity.FavoriteEntity
import com.srgpanov.simpleweather.data.models.entity.OneCallEntity
import com.srgpanov.simpleweather.data.models.entity.SimpleWeatherEntity
import com.srgpanov.simpleweather.data.models.ip_to_location.IpToLocation
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.places.Places
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.SimpleWeatherResponse
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val localDataSource: LocalDataSourceImpl,
    private val remoteDataSource: RemoteDataSourceImpl
) {


    suspend fun getFreshWeather(geoPoint: GeoPoint): OneCallResponse? {
        val response = remoteDataSource.getOneCallWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon
        )
        return when (response) {
            is ResponseResult.Success ->
                response.data.also { saveWeatherResponse(response.data) }

            is ResponseResult.Failure -> null
        }
    }


    suspend fun getSimpleFreshWeather(geoPoint: GeoPoint): ResponseResult<SimpleWeatherResponse> {
        val response = remoteDataSource.getCurrentWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon
        )
        if (response is ResponseResult.Success) {
            logD("getWeather return fresh  response ")
            coroutineScope { saveSimpleResponse(response.data) }
            logD("fresh  response saved  ")
        }
        return response
    }

    private suspend fun saveSimpleResponse(response: SimpleWeatherResponse) {
        val placeId = response.getGeoPoint().pointToId()
        val responseEntity = SimpleWeatherEntity(
            id = placeId,
            placeId = placeId,
            simpleWeatherResponse = response
        )
        localDataSource.saveCurrentResponse(responseEntity)
    }

    private suspend fun saveWeatherResponse(response: OneCallResponse) {
        val placeId = response.getGeoPoint().pointToId()
        val responseEntity = OneCallEntity(
            id = placeId,
            placeId = placeId,
            oneCallResponse = response
        )
        localDataSource.saveOneCallResponse(responseEntity)
    }

    fun getFavoritePlaces(): Flow<List<PlaceViewItem>> {
        return localDataSource.getFavoritesPlaces()
    }

    fun getCurrentPlaceFlow(): Flow<PlaceViewItem?> {
        return localDataSource.getCurrentLocationFlow()
    }

    suspend fun getCurrentPlace(): PlaceViewItem? {
        return localDataSource.getCurrentLocation()
    }

    suspend fun saveCurrentPlace(placeViewItem: PlaceViewItem) {
        val currentPlace: CurrentEntity = placeViewItem.toCurrentEntity()
        localDataSource.saveCurrentPlace(currentPlace)
    }

    suspend fun saveFavoritePlace(placeViewItem: PlaceViewItem) {
        val currentPlace: FavoriteEntity = placeViewItem.toFavoriteTable()
        localDataSource.saveFavoritePlace(currentPlace)
    }

    suspend fun placeIsFavorite(placeViewItem: PlaceViewItem): Boolean {
        return localDataSource.placeIsFavorite(placeViewItem)
    }

    suspend fun placeIsCurrent(placeViewItem: PlaceViewItem): Boolean {
        return localDataSource.getCurrentLocationFlow().toList().firstOrNull()
            ?.toGeoPoint() == placeViewItem.toGeoPoint()
    }

    fun placeIsFavoriteFlow(id: String): Flow<FavoriteEntity?> {
        return localDataSource.placeIsFavoriteFlow(id)
    }

    suspend fun removeFavoritePlace(placeViewItem: PlaceViewItem) {
        localDataSource.removeFavoritePlace(placeViewItem)
    }

    suspend fun savePlaceToHistory(placeViewItem: PlaceViewItem) {
        localDataSource.savePlaceToHistory(placeViewItem)
    }

    suspend fun getSearchHistory(): List<PlaceViewItem> {
        return localDataSource.getSearchHistory()
    }

    suspend fun getPlaceByGeoPoint(geoPoint: GeoPoint): PlaceViewItem? {
        logD("getPlaceByGeoPoint geoPoint ${geoPoint.pointToId()}")
        val place = localDataSource.getPlace(geoPoint.pointToId())
        if (place != null) {
            logD("getPlaceByGeoPoint returned from DB")
            return place.toPlaceEntity()
        } else {
            return when (val responseResult = getPlaces(geoPoint.pointToQuery())) {
                is ResponseResult.Success -> {
                    localDataSource.savePlace(responseResult.data.toPlaceItem())
                    logD("getPlaceByGeoPoint returned from remote")
                    responseResult.data.toPlaceItem()
                }
                is ResponseResult.Failure -> {
                    logD("getPlaceByGeoPoint returned null")
                    null
                }
            }
        }


    }

    fun getPlaceByGeoPointFlow(geoPoint: GeoPoint): Flow<PlaceViewItem> {
        val place = localDataSource.getPlaceFlow(geoPoint.pointToId())
        return place.map { it.toPlaceEntity() }
    }

    suspend fun getPlaces(query: String): ResponseResult<Places> {
        return remoteDataSource.getPlaces(query)
    }

    suspend fun getGeoPointFromIp(): ResponseResult<IpToLocation> {
        return remoteDataSource.getGeoPointFromIp()
    }


    suspend fun getOneCallTable(geoPoint: GeoPoint): OneCallEntity? {
        return localDataSource.getOneCallResponse(geoPoint)
    }

    fun getOneCallResponseFlow(geoPoint: GeoPoint): Flow<OneCallEntity?> {
        return localDataSource.getOneCallResponseFlow(geoPoint)
    }

    suspend fun savePlace(placeViewItem: PlaceViewItem) {
        localDataSource.savePlace(placeViewItem)
    }

    suspend fun renamePlace(placeViewItem: PlaceViewItem) {
        localDataSource.updatePlace(placeViewItem)
    }

    suspend fun placeIsInDb(shownGeoPoint: GeoPoint): Boolean {
        return localDataSource.placeIsInDb(shownGeoPoint)
    }

}