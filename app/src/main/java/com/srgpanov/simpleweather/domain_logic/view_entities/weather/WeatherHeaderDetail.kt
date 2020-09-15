package com.srgpanov.simpleweather.domain_logic.view_entities.weather

data class WeatherHeaderDetail(
    val weatherDetailCurrent: WeatherDetailCurrent,
    val weatherDetailHourly: List<WeatherDetailHourly>
)