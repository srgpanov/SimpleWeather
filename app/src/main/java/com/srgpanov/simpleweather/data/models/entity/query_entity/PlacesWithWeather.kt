package com.srgpanov.simpleweather.data.models.entity.query_entity

import androidx.room.Embedded
import androidx.room.Relation
import com.srgpanov.simpleweather.data.models.entity.CurrentEntity
import com.srgpanov.simpleweather.data.models.entity.OneCallEntity
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem

data class PlacesWithWeather(
    @Embedded
    val placeEntity: PlaceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    var oneCallEntity: OneCallEntity?
) {
    fun toCurrentTable(): CurrentEntity {
        return CurrentEntity(placeEntity.id)
    }

    fun toPlaceEntity(): PlaceViewItem {
        return PlaceViewItem(
            title = placeEntity.title,
            lat = placeEntity.lat,
            lon = placeEntity.lon,
            cityFullName = placeEntity.fullName,
            oneCallResponse = oneCallEntity?.oneCallResponse
        )
    }
}