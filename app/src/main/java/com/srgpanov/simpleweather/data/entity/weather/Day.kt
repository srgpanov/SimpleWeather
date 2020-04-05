package com.srgpanov.simpleweather.data.entity.weather

data class Day(
    val _source: String,
    val cloudness: Float,
    val condition: String,
    val daytime: String,
    val feels_like: Int,
    val humidity: Int,
    val icon: String,
    val polar: Boolean,
    val prec_mm: Int,
    val prec_period: Int,
    val prec_prob: Int,
    val prec_strength: Float,
    val prec_type: Int,
    val pressure_mm: Int,
    val pressure_pa: Int,
    val soil_moisture: Float,
    val soil_temp: Int,
    val temp_avg: Int,
    val temp_max: Int,
    val temp_min: Int,
    val uv_index: Int,
    val wind_dir: String,
    val wind_gust: Float,
    val wind_speed: Float
)