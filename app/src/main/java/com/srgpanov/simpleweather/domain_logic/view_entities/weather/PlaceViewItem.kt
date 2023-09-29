package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import android.os.Parcelable
import com.srgpanov.simpleweather.data.models.entity.CurrentEntity
import com.srgpanov.simpleweather.data.models.entity.FavoriteEntity
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.SearchHistoryEntity
import com.srgpanov.simpleweather.data.models.entity.SimpleWeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceViewItem(
    val title: String,
    val lat: Double,
    val lon: Double,
    val cityFullName: String? = null,
    val favorite: Boolean = false,
    val current: Boolean = false,
    val simpleWeather: SimpleWeatherEntity? = null,
    val oneCallResponse: OneCallResponse? = null
) : Parcelable {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(lat, lon)
    }

    fun toPlaceId(): String {
        return toGeoPoint().pointToId()
    }

    fun toCurrentEntity(): CurrentEntity {
        return CurrentEntity(
            id = toGeoPoint().pointToId()
        )
    }

    fun toFavoriteTable(): FavoriteEntity {
        return FavoriteEntity(
            id = toGeoPoint().pointToId()
        )
    }

    fun toSearchHistoryTable(): SearchHistoryEntity {
        return SearchHistoryEntity(
            id = toGeoPoint().pointToId()
        )
    }

    fun toPlaceTable(): PlaceEntity {
        return PlaceEntity(
            id = GeoPoint(lat, lon).pointToId(),
            lat = lat,
            lon = lon,
            title = title,
            fullName = cityFullName
        )
    }
}