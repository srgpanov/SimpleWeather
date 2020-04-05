package com.srgpanov.simpleweather.data.entity.weather

data class Fact(
    val accum_prec: AccumPrec,
    val cloudness: Float,
    val condition: String,
    val daytime: String,
    val feels_like: Int,
    val humidity: Int,
    val icon: String,
    val obs_time: Int,
    val polar: Boolean,
    val prec_strength: Float,
    val prec_type: Int,
    val pressure_mm: Int,
    val pressure_pa: Int,
    val season: String,
    val soil_moisture: Float,
    val soil_temp: Int,
    val source: String,
    val temp: Int,
    val uv_index: Int,
    val wind_dir: String,
    val wind_gust: Float,
    val wind_speed: Float
)