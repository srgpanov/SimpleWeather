package com.srgpanov.simpleweather.data.entity.weather

data class Tzinfo(
    val abbr: String,
    val dst: Boolean,
    val name: String,
    val offset: Int
)