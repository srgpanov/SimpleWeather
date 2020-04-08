package com.srgpanov.simpleweather.data.local

import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
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
        return dao.getCurrentLocation()
    }
    override suspend fun savePlace(placeEntity: PlaceEntity) {

            dao.savePlace(placeEntity)
            logD("saved place")


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

    override suspend fun getPlace(geoPoint: GeoPoint): PlaceEntity? {
        return dao.getPlace(geoPoint.lat,geoPoint.lon)
    }

    override suspend fun changeFavoriteStatus(placeEntity: PlaceEntity) {
        val  isFavorite = placeEntity.isFavorite==1
        val changedPlace:PlaceEntity
        if (isFavorite){
            changedPlace= placeEntity.copy(isFavorite = 0)
        }else{
            changedPlace=placeEntity.copy(isFavorite = 1)
        }
        dao.changeFavoriteStatus(changedPlace)
    }


}