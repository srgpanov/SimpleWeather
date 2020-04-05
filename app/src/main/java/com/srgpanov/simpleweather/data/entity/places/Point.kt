package com.srgpanov.simpleweather.data.entity.places

import com.srgpanov.simpleweather.data.entity.GeoPoint

data class Point(
    val pos: String
){
    fun getGeoPoint():GeoPoint{
        try {
            val lon = pos.split(" ")[0].toDouble()
            val lat = pos.split(" ")[1].toDouble()
            return GeoPoint(lat,lon)
        }catch (e:Exception){
            return GeoPoint()
        }
    }
}