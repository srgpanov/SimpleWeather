package com.srgpanov.simpleweather.data.models.places


import com.google.gson.annotations.SerializedName

data class FeatureMember(
    @SerializedName("GeoObject")
    val geoObject: GeoObject
) {
    fun getFormattedName(): String {
        val place =
            geoObject.metaDataProperty.geocoderMetaData.address.formatted.split(",")
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