package com.srgpanov.simpleweather.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.TimeCounter
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse

@Entity()
data class OneCallTable(
    @PrimaryKey
    val id:String,
    val oneCallResponse: OneCallResponse,
    override val time:Long=System.currentTimeMillis()
):TimeCounter