package com.srgpanov.simpleweather.data.entity.weather

data class Parts(
    val day: Day,
    val day_short: DayShort,
    val evening: Evening,
    val morning: Morning,
    val night: Night,
    val night_short: NightShort
)