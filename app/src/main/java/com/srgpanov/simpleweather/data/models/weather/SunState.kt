package com.srgpanov.simpleweather.data.models.weather


sealed class SunState(open val time: String) {
    data class Sunrise(override val time: String) : SunState(time)
    data class Sunset(override val time: String) : SunState(time)
}