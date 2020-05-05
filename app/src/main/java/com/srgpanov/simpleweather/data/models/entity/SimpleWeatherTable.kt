package com.srgpanov.simpleweather.data.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.TimeCounter
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse

@Entity()
data class SimpleWeatherTable (
    @PrimaryKey
    val id:String,
    val currentWeatherResponse: CurrentWeatherResponse,
    override val time:Long=System.currentTimeMillis()
):TimeCounter