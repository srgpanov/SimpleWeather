package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse


data class PlaceEntity(
    @ColumnInfo(name = "cityTitle")
    val cityTitle: String,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lon")
    val lon: Double,
    @ColumnInfo(name = "cityFullName")
    val cityFullName: String? = ""
) : Parcelable {
    @Ignore
    var favorite: Boolean = false
    @Ignore
    var current: Boolean = false
    @Ignore
    var simpleWeatherTable: SimpleWeatherTable? = null
    @Ignore
    var oneCallResponse: OneCallResponse? = null

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(lat, lon)
    }

    fun toPlaceId(): String {
        return toGeoPoint().pointToId()
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

    //<editor-fold desc="Parcelize">
    constructor(source: Parcel) : this(
        source.readString().toString(),
    source.readDouble(),
    source.readDouble(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(cityTitle)
        writeDouble(lat)
        writeDouble(lon)
        writeString(cityFullName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PlaceEntity> = object : Parcelable.Creator<PlaceEntity> {
            override fun createFromParcel(source: Parcel): PlaceEntity = PlaceEntity(source)
            override fun newArray(size: Int): Array<PlaceEntity?> = arrayOfNulls(size)
        }
    }
    //</editor-fold>
}