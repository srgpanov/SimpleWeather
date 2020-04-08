package com.srgpanov.simpleweather.data.models.weather

data class Info(
    val _h: Boolean,
    val def_pressure_mm: Int,
    val def_pressure_pa: Int,
    val f: Boolean,
    val lat: Double,
    val lon: Double,
    val n: Boolean,
    val nr: Boolean,
    val ns: Boolean,
    val nsr: Boolean,
    val p: Boolean,
    val tzinfo: Tzinfo,
    val url: String
)