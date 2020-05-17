package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceEntity(
    val title: String,
    val lat: Double,
    val lon: Double,
    val cityFullName: String? = null,
    var favorite: Boolean = false,
    var current: Boolean = false,
    var simpleWeather: SimpleWeatherTable? = null,
    var oneCallResponse: OneCallResponse? = null
) : Parcelable {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(lat, lon)
    }

    fun toPlaceId(): String {
        return toGeoPoint().pointToId()
    }

    fun toCurrentTable(): CurrentTable {
        return CurrentTable(
            id = toGeoPoint().pointToId()
        )
    }

    fun toFavoriteTable(): FavoriteTable {
        return FavoriteTable(
            id = toGeoPoint().pointToId()
        )
    }

    fun toSearchHistoryTable(): SearchHistoryTable {
        return SearchHistoryTable(
            id = toGeoPoint().pointToId()
        )
    }

    fun toPlaceTable(): PlaceTable {
        return PlaceTable(
            id=GeoPoint(lat,lon).pointToId(),
            lat = lat,
            lon = lon,
            title = title,
            fullName = cityFullName
        )
    }


}