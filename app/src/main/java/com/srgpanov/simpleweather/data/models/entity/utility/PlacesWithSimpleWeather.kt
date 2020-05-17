package com.srgpanov.simpleweather.data.models.entity.utility

import androidx.room.Embedded
import androidx.room.Relation
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.PlaceTable
import com.srgpanov.simpleweather.data.models.entity.SimpleWeatherTable

data class PlacesWithSimpleWeather(
    @Embedded
    val placeTable: PlaceTable,
    @Relation(
        parentColumn = "id",
        entityColumn ="placeId"
    )
    val simpleWeatherTable: SimpleWeatherTable?
) {
    fun toPlaceEntity():PlaceEntity{
        return PlaceEntity(
            title = placeTable.title,
            favorite = true,
            lat = placeTable.lat,
            lon = placeTable.lon,
            cityFullName = placeTable.fullName,
            simpleWeather = simpleWeatherTable
        )
    }
}