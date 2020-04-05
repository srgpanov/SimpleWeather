package com.srgpanov.simpleweather.data.entity.places

data class GeoObject(
    val Point: Point,
    val boundedBy: BoundedBy,
    val description: String,
    val metaDataProperty: MetaDataProperty,
    val name: String
)