package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize

data class PlaceEntity (
    @ColumnInfo(name = "cityTitle")
    val cityTitle:String,
    @ColumnInfo(name = "lat")
    val lat:Double,
    @ColumnInfo(name = "lon")
    val lon:Double,
    @ColumnInfo(name = "cityFullName")
    val cityFullName:String?=""
) : Parcelable {
    @Ignore
    var favorite:Boolean=false
    @Ignore
    var current :Boolean=false


    fun toGeoPoint():GeoPoint{
        return GeoPoint(lat,lon)
    }
    fun toCurrentTable(): CurrentTable {
        return CurrentTable(
            id = toGeoPoint().pointToId(),
            cityFullName = cityFullName,
            lat = lat,
            lon = lon,
            cityTitle = cityTitle
        )
    }
    fun toFavoriteTable(): FavoriteTable {
        return FavoriteTable(
            id = toGeoPoint().pointToId(),
            cityFullName = cityFullName,
            lat = lat,
            lon = lon,
            cityTitle = cityTitle
        )
    }
    fun toSearchHistoryTable(): SearchHistoryTable {
        return SearchHistoryTable(
            id = toGeoPoint().pointToId(),
            cityFullName = cityFullName,
            lat = lat,
            lon = lon,
            cityTitle = cityTitle
        )
    }

}