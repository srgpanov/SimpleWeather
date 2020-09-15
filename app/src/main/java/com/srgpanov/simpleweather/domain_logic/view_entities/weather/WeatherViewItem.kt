package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import com.srgpanov.simpleweather.data.models.weather.OneCallResponse

data class WeatherViewItem(
    val oneCallResponse: OneCallResponse,
    val header: WeatherHeader,
    val dayList: List<Days>
)