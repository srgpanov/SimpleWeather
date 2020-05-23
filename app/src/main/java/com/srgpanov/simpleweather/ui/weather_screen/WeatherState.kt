package com.srgpanov.simpleweather.ui.weather_screen

import com.srgpanov.simpleweather.data.models.weather.OneCallResponse

sealed class WeatherState {
    object EmptyWeather : WeatherState()
    data class ActualWeather(val oneCallResponse: OneCallResponse):WeatherState()
    object ErrorWeather:WeatherState()
}