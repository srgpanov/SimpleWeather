package com.srgpanov.simpleweather.data

import android.content.Context
import com.srgpanov.simpleweather.data.local.LocalDataSource
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.entity.CurrentTable
import com.srgpanov.simpleweather.data.models.entity.FavoriteTable
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse
import com.srgpanov.simpleweather.data.remote.RemoteDataSource
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.other.logD
import java.util.*

class DataRepositoryImpl(val context: Context) : DataRepository {
    private val localDataSource: LocalDataSource = LocalDataSourceImpl()
    private val remoteDataSource: RemoteDataSource = RemoteDataSourceImpl()
    private val refreshTime = 3600000L //1 hour

    override suspend fun getWeather(geoPoint: GeoPoint): WeatherResponse? {
        val cachedResponse = localDataSource.getResponse(geoPoint)
        if (cachedResponse == null) {
            logD("getWeather return null ")
            return getFreshWeather(geoPoint)
        } else {
            if (needRefresh(cachedResponse)) {
                logD("getWeather return getFreshWeather ")
                return getFreshWeather(geoPoint)
            } else {
                logD("getWeather return cachedResponse ${cachedResponse.response.now_dt} ")
                return cachedResponse.response
            }
        }
    }

    private fun needRefresh(cachedResponse: WeatherEntity): Boolean {
        val timeFromLastResponse = System.currentTimeMillis() - cachedResponse.time
        logD("time from last request "+Date(timeFromLastResponse).toString())
        return timeFromLastResponse > refreshTime
    }


    override suspend fun getFreshWeather(geoPoint: GeoPoint): WeatherResponse? {
        val response = remoteDataSource.getWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon
        )
        response?.let {
            logD("getWeather return fresh  response ")

            saveWeatherResponse(response)
            logD("fresh  response saved  ${it.now_dt}")
        }
        return response
    }
    override suspend fun getSimpleFreshWeather(geoPoint: GeoPoint): WeatherResponse? {
        val response = remoteDataSource.getWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon,
            day = 2,
            extra = false
        )
        response?.let {
            logD("getWeather return fresh  response ")

            saveWeatherResponse(response)
            logD("fresh  response saved  ${it.now_dt}")
        }
        return response
    }

    override suspend fun getSimpleWeather(geoPoint: GeoPoint): WeatherResponse? {
        val cachedResponse = localDataSource.getResponse(geoPoint)
        if (cachedResponse == null) {
            logD("getWeather return null ")
            return getSimpleFreshWeather(geoPoint)
        } else {
            if (needRefresh(cachedResponse)) {
                logD("getWeather return getFreshWeather ")
                return getSimpleFreshWeather(geoPoint)
            } else {
                logD("getWeather return cachedResponse ${cachedResponse.response.now_dt} ")
                return cachedResponse.response
            }
        }
    }

    override suspend fun getFavoritePlaces(): List<PlaceEntity> {
        return localDataSource.getFavoritesPlaces()
    }

    override suspend fun getCurrentPlace(): PlaceEntity? {
        return localDataSource.getCurrentLocation()
    }

    override suspend fun saveWeatherResponse(response: WeatherResponse) {
        val responseEntity = WeatherEntity(
            id = response.getGeoPoint().pointToId(),
            response = response,
            time = System.currentTimeMillis()
        )
        localDataSource.saveRequest(responseEntity)
    }

    override suspend fun saveCurrentPlace(placeEntity: PlaceEntity) {
        val currentPlace:CurrentTable=placeEntity.toCurrentTable()
        localDataSource.saveCurrentPlace(currentPlace)

    }

    override suspend fun saveFavoritePlace(placeEntity: PlaceEntity) {
        val currentPlace:FavoriteTable=placeEntity.toFavoriteTable()
        localDataSource.saveFavoritePlace(currentPlace)
    }

    override suspend fun placeIsFavorite(placeEntity: PlaceEntity): Boolean {
        return localDataSource.placeIsFavorite(placeEntity)
    }

    override suspend fun placeIsCurrent(placeEntity: PlaceEntity): Boolean {
        return localDataSource.getCurrentLocation()?.toGeoPoint()==placeEntity.toGeoPoint()
    }

    override suspend fun removeFavoritePlace(placeEntity: PlaceEntity) {
        localDataSource.removeFavoritePlace(placeEntity)
    }

    override suspend fun savePlaceToHistory(placeEntity: PlaceEntity) {
        localDataSource.savePlaceToHistory(placeEntity)
    }

    override suspend fun getSearchHistory(): List<PlaceEntity> {
        return localDataSource.getSearchHistory()
    }


}