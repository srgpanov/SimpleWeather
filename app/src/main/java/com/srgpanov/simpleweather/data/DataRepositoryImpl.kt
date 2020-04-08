package com.srgpanov.simpleweather.data

import android.content.Context
import com.srgpanov.simpleweather.data.local.LocalDataSource
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
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
        logD(Date(timeFromLastResponse).toString())
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

    override suspend fun getSimpleWeather(geoPoint: GeoPoint): WeatherResponse? {
        val response = remoteDataSource.getWeather(
            lat = geoPoint.lat,
            lon = geoPoint.lon,
            day = 1,
            hours = false,
            extra = false
        )
        logD("getSimpleWeather return   response ${response?.now_dt}")
        //todo make save?
//        response?.let {
//            logD("getSimpleWeather return   response ")
//
//            saveWeatherResponse(response)
//            logD("fresh  response saved  ${it.now_dt}")
//        }
        return response
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

    override suspend fun savePlace(placeEntity: PlaceEntity) {
        localDataSource.savePlace(placeEntity)

    }

    override suspend fun getPlace(geoPoint: GeoPoint): PlaceEntity? {
        val cachePlace = localDataSource.getPlace(geoPoint)
        if (cachePlace == null) {
            val place = remoteDataSource.getPlaces(
                "${geoPoint.lon},${geoPoint.lat}"

            )
            if (place != null) {
                logD("place loaded remote ${place.toEntity().cityTitle}")
                val placeEntity = place.toEntity()
                localDataSource.savePlace(placeEntity)
                return placeEntity

            }else{
                logD("place not loaded remote and local")
                return null
            }
        }else{
            logD("place  loaded local $cachePlace")
            return cachePlace
        }
    }

    override suspend fun changeFavotiteStatus(placeEntity: PlaceEntity) {
        localDataSource.changeFavoriteStatus(placeEntity)
    }

}