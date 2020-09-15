package com.srgpanov.simpleweather.ui.weather_screen

import com.srgpanov.simpleweather.domain_logic.view_entities.weather.WeatherViewItem

sealed class WeatherState {
    object EmptyWeather : WeatherState()
    data class ActualWeather(val weatherViewItem: WeatherViewItem, val error: Throwable? = null) :
        WeatherState()
    object ErrorWeather:WeatherState()
}