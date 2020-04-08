package com.srgpanov.simpleweather.data.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse

@Entity()
data class WeatherEntity (
    @PrimaryKey
    val id:String,

    val response: WeatherResponse,
    val time:Long
){

}