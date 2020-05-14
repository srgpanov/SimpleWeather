package com.srgpanov.simpleweather.data.models.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.RoundingMode

@Parcelize
data class GeoPoint(
    val lat: Double = 0.0,
    val lon: Double = 0.0
) : Parcelable {
    fun pointToId(): String {
        val latRounded = lat.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        val lonRounded = lon.toBigDecimal().setScale(2, RoundingMode.DOWN).toDouble()
        return "lat:$latRounded;lon:$lonRounded"
    }
    fun pointToQuery():String{
        return "${lon},${lat}"
    }
}