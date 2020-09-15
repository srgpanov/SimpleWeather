package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import com.srgpanov.simpleweather.ui.weather_screen.WeatherState

data class PlaceWithWeather(
    val title: String,
    val cityFullName: String? = null,
    val favorite: Boolean = false,
    val current: Boolean = false,
    val weather: WeatherState
)