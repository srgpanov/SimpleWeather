package com.srgpanov.simpleweather.data.models.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoPoint(
    val lat:Double=0.0,
    val lon:Double=0.0
) : Parcelable {
    fun pointToId():String{
        return "lat:$lat;lon:$lon"
    }
}