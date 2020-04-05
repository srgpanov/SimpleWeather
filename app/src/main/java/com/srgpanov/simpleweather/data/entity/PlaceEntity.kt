package com.srgpanov.simpleweather.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class PlaceEntity (
    @PrimaryKey
    val cityTitle:String,
    @Embedded
    val geoPoint: GeoPoint,
    val isCurrent:Int=0,
    val cityFullName:String?=""

)