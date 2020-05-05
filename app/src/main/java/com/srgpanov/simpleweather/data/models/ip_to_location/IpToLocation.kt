package com.srgpanov.simpleweather.data.models.ip_to_location


import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.other.GeoPoint

data class IpToLocation(
    @SerializedName("city")
    val city: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("query")
    val query: String,
    @SerializedName("status")
    val status: String
){
    fun toGeoPoint():GeoPoint{
        return GeoPoint(lat,lon)
    }
}