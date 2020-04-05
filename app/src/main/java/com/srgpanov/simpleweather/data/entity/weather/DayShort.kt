package com.srgpanov.simpleweather.data.entity.weather

data class DayShort(
    val _source: String,
    val cloudness: Float,
    val condition: String,
    val feels_like: Int,
    val humidity: Int,
    val icon: String,
    val prec_mm: Int,
    val prec_prob: Int,
    val prec_strength: Int,
    val prec_type: Int,
    val pressure_mm: Int,
    val pressure_pa: Int,
    val soil_moisture: Float,
    val soil_temp: Int,
    val temp: Int,
    val temp_min: Int,
    val uv_index: Int,
    val wind_dir: String,
    val wind_gust: Float,
    val wind_speed: Float
)