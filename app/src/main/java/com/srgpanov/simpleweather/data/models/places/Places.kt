package com.srgpanov.simpleweather.data.models.places

import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity

data class Places(

    @SerializedName("response")
    val response: PlacesResponse
) {
    fun toEntity(): PlaceEntity {
        return PlaceEntity(
            cityTitle = response.GeoObjectCollection.featureMember[0].GeoObject.name,
            geoPoint = response.GeoObjectCollection.featureMember[0].GeoObject.Point.getGeoPoint()

        )
    }
}