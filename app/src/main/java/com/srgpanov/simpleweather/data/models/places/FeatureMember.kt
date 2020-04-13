package com.srgpanov.simpleweather.data.models.places

data class FeatureMember(
    val GeoObject: GeoObject
) {
    fun getFormatedName(): String {
        val place =
            GeoObject.metaDataProperty.GeocoderMetaData.Address.formatted.split(",")
                .reversed()
        val builder = StringBuilder()
        place.forEachIndexed { index, string ->
            if (index == place.size - 1) {
                builder.append(string.trim())
            } else {
                builder.append(string.trim()).append(", ")
            }
        }
        return builder.toString()
    }
}
