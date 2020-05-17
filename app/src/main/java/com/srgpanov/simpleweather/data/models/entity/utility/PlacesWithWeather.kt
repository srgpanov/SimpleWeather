package com.srgpanov.simpleweather.data.models.entity.utility

import androidx.room.Embedded
import androidx.room.Relation
import com.srgpanov.simpleweather.data.models.entity.*

data class PlacesWithWeather(
    @Embedded
    val placeTable: PlaceTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    var oneCallTable: OneCallTable?
) {
    fun toCurrentTable():CurrentTable{
        return CurrentTable(placeTable.id)
    }
    fun toPlaceEntity(): PlaceEntity {
        return PlaceEntity(
            title = placeTable.title,
            lat = placeTable.lat,
            lon = placeTable.lon,
            cityFullName = placeTable.fullName,
            oneCallResponse = oneCallTable?.oneCallResponse
        )
    }
}