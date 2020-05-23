package com.srgpanov.simpleweather.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceTable(
    @PrimaryKey
    val id: String,
    val title: String,
    val lat: Double,
    val lon: Double,
    val fullName: String? = null
) {
    fun toPlaceEntity(): PlaceEntity {
        return PlaceEntity(title = title,lat = lat, lon = lon, cityFullName = fullName)
    }
}