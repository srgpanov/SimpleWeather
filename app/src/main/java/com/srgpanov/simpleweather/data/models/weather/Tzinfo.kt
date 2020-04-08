package com.srgpanov.simpleweather.data.models.weather

data class Tzinfo(
    val abbr: String,
    val dst: Boolean,
    val name: String,
    val offset: Int
)