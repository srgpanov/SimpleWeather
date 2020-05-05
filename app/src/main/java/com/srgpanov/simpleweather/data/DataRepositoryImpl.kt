package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.TimeCounter
import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.ip_to_location.IpToLocation
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.places.Places
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.coroutineScope
import java.util.*

class DataRepositoryImpl() {
    private val localDataSource = LocalDataSourceImpl()
    private val remoteDataSource = RemoteDataSourceImpl()

    companion object {
        const val REFRESH_TIME = 3600000L //1 hour
    }

    suspend fun getWeather(geoPoint: GeoPoint, freshData: Boolean = true): ResponseResult<OneCallResponse> {
        val cachedResponse = localDataSource.getOneCallResponse(geoPoint)
        if (cachedResponse == null) {
            logD("getWeather return null ")
            return getFreshWeather(geoPoint)
        } else {
            if (freshData and needRefresh(cachedResponse, REFRESH_TIME)) {
                logD("getWeather return getFreshWeather ")
                return getFreshWeather(geoPoint)
            } else {
                logD("getWeather return cachedResponse ${cachedResponse.oneCallResponse.current.dt} ")
                return ResponseResult.Success(cachedResponse.oneCallResponse)
            }
        }
    }

    suspend fun getFreshWeather(geoPoint: GeoPoint): ResponseResult<OneCallResponse> {
        val response = remoteDataSource.getOneCallWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon
        )
        logD("getFreshWeather $response")
        if (response is ResponseResult.Success) {
            response.data
            logD("getWeather return fresh  response ")

            coroutineScope {saveWeatherResponse(response.data)}
            logD("fresh  response saved  ${response.data.current.dt}")
        }
        return response
    }


    suspend fun getSimpleFreshWeather(geoPoint: GeoPoint): ResponseResult<CurrentWeatherResponse> {
        val response = remoteDataSource.getCurrentWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon
        )
        if (response is ResponseResult.Success){
            logD("getWeather return fresh  response ")
            coroutineScope { saveCurrentResponse(response.data) }
            logD("fresh  response saved  ")
        }
        return response
    }

    private suspend fun saveCurrentResponse(response: CurrentWeatherResponse) {
        val responseEntity = SimpleWeatherTable(
            id = response.getGeoPoint().pointToId(),
            currentWeatherResponse = response,
            time = System.currentTimeMillis()
        )
        localDataSource.saveCurrentResponse(responseEntity)
    }

    suspend fun getSimpleWeather(geoPoint: GeoPoint): ResponseResult<CurrentWeatherResponse> {
        val cachedResponse = localDataSource.getCurrentResponse(geoPoint)
        if (cachedResponse == null) {
            logD("getSimpleWeather return null ")
            return getSimpleFreshWeather(geoPoint)
        } else {
            if (needRefresh(cachedResponse, REFRESH_TIME)) {
                logD("getWeather return getFreshWeather ")
                return getSimpleFreshWeather(geoPoint)
            } else {
                logD("getWeather return cachedResponse ${cachedResponse.currentWeatherResponse.dt} ")
                return ResponseResult.Success(cachedResponse.currentWeatherResponse)
            }
        }
    }

    suspend fun getFavoritePlaces(): List<PlaceEntity> {
        return localDataSource.getFavoritesPlaces()
    }

    suspend fun getCurrentPlace(): PlaceEntity? {
        return localDataSource.getCurrentLocation()
    }

    suspend fun saveWeatherResponse(response: OneCallResponse) {
        val responseEntity = OneCallTable(
            id = response.getGeoPoint().pointToId(),
            oneCallResponse = response,
            time = System.currentTimeMillis()
        )
        localDataSource.saveOneCallResponse(responseEntity)
    }

    suspend fun saveCurrentPlace(placeEntity: PlaceEntity) {
        val currentPlace: CurrentTable = placeEntity.toCurrentTable()
        localDataSource.saveCurrentPlace(currentPlace)

    }

    suspend fun saveFavoritePlace(placeEntity: PlaceEntity) {
        val currentPlace: FavoriteTable = placeEntity.toFavoriteTable()
        localDataSource.saveFavoritePlace(currentPlace)
    }

    suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean {
        return localDataSource.placeIsFavorite(placeEntity)
    }

    suspend fun placeIsCurrent(placeEntity: PlaceEntity): Boolean {
        return localDataSource.getCurrentLocation()?.toGeoPoint() == placeEntity.toGeoPoint()
    }

    suspend fun removeFavoritePlace(placeEntity: PlaceEntity) {
        localDataSource.removeFavoritePlace(placeEntity)
    }

    suspend fun savePlaceToHistory(placeEntity: PlaceEntity) {
        localDataSource.savePlaceToHistory(placeEntity)
    }

    suspend fun getSearchHistory(): List<PlaceEntity> {
        return localDataSource.getSearchHistory()
    }

    private fun needRefresh(response: TimeCounter, refreshTime: Long): Boolean {
        val timeFromLastResponse = System.currentTimeMillis() - response.time
        logD("time from last request " + Date(timeFromLastResponse).toString())
        return timeFromLastResponse > refreshTime
    }

    suspend fun getPlaceByGeoPoint(geoPoint: GeoPoint): ResponseResult<Places> {
        return remoteDataSource.getPlaces(geoPoint.pointToQuery())

    }

    suspend fun getGeoPointFromIp(): ResponseResult<IpToLocation> {
        return remoteDataSource.getGeoPointFromIp()
    }

    suspend fun getPlaceFromIp(): ResponseResult<Places> {
        val response = getGeoPointFromIp()
        return when (response){
            is ResponseResult.Success -> getPlaceByGeoPoint(response.data.toGeoPoint())
            is ResponseResult.Failure.ServerError -> TODO()
            is ResponseResult.Failure.NetworkError -> TODO()
        }
    }

    suspend fun getCachedWeather(geoPoint: GeoPoint):OneCallResponse? {
        val oneCallResponse = localDataSource.getOneCallResponse(geoPoint)
        return oneCallResponse?.oneCallResponse

    }

}