package com.srgpanov.simpleweather.data.entity.places

data class Address(
    val Components: List<Component>,
    val country_code: String,
    val formatted: String
)