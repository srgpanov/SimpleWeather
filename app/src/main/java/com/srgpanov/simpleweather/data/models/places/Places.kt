package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity

data class Places(
    @SerializedName("response")
    val response: Response
){
    fun toEntity():PlaceEntity{
        return PlaceEntity(
            title = response.geoObjectCollection.featureMember[0].geoObject.name,
            lat = response.geoObjectCollection.metaDataProperty.geocoderResponseMetaData.point.toGeoPoint().lat,
            lon = response.geoObjectCollection.metaDataProperty.geocoderResponseMetaData.point.toGeoPoint().lon,
            cityFullName = response.geoObjectCollection.featureMember[0].getFormattedName(),
            favorite = false,
            current = false,
            simpleWeather = null,
            oneCallResponse = null

        )
    }
}