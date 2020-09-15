package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logE

data class Point(
    @SerializedName("pos")
    val pos: String
){
    fun toGeoPoint():GeoPoint{

        return try {
            logD("toGeoPoint lat = ${pos.split(" ")[0].toDouble()} lon = ${pos.split(" ")[1].toDouble()}")
            val splittedString = pos.split(" ")
            GeoPoint(
                lat = splittedString[1].toDouble(),
                lon = splittedString[0].toDouble()
            )
        } catch (e: Exception) {
            logE("toGeoPoint somethings goes wrong pos $pos ")
            GeoPoint()
        }
    }
}
