package com.srgpanov.simpleweather.data

import com.srgpanov.simpleweather.data.entity.PlaceEntity
import com.srgpanov.simpleweather.ui.App

class LocalDataSourceImpl : LocalDataSource {
    val dao = WeatherDataBase.getInstance(App.instance).weatherDataDao()
    override suspend fun getFavoritesPlaces(): List<PlaceEntity> {
        return dao.getFavoritesPlaces()
    }
    override suspend fun getCurrentLocation(): List<PlaceEntity> {
        return dao.getCurrentLocation()
    }
    override suspend fun savePlace(placeEntity: PlaceEntity) {
        dao.savePlace(placeEntity)
    }
//    override suspend fun getFavoritesPlaces(): List<FeatureMember> {
//        val favoritesPlaces = dao.getFavoritesPlaces()
//        val list = mutableListOf<FeatureMember>()
//        favoritesPlaces.forEach {
//            list.add(it.featureMember)
//        }
//        return list
//    }

//    override suspend fun getCurrentLocation(): FeatureMember {
//        return dao.getCurrentLocation().featureMember
//    }

//    override suspend fun getLastRequest(): List<WeatherEntity> {
//        val lastResponse = dao.getLastResponse()
//        logD(lastResponse.toString())
//        return dao.getLastResponse()
//    }
//
//    override suspend fun saveRequest(weatherEntity: WeatherEntity) {
//        dao.saveResponse(weatherEntity)
//    }

//    override suspend fun savePlace(placesEntity: PlacesEntity) {
//        dao.savePlace(placesEntity)
//    }
}