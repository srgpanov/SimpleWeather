package com.srgpanov.simpleweather.data.entity.places

data class Country(
    val AddressLine: String,
    val AdministrativeArea: AdministrativeArea,
    val Country: CountryX,
    val CountryName: String,
    val CountryNameCode: String
)