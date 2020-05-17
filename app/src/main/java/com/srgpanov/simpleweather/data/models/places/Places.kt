package com.srgpanov.simpleweather.data.models.places

import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity

data class Places(

    @SerializedName("response")
    val response: PlacesResponse
) {
    fun toEntity(): PlaceEntity {
        return PlaceEntity(
            title = response.GeoObjectCollection.featureMember[0].GeoObject.name,
            lat = response.GeoObjectCollection.featureMember[0].GeoObject.Point.getGeoPoint().lat,
            lon = response.GeoObjectCollection.featureMember[0].GeoObject.Point.getGeoPoint().lon

        )
    }
}