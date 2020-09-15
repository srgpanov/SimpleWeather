package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.weather.current_weather.SimpleWeatherResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            deferred = true,
            onDelete = ForeignKey.CASCADE
        )], tableName = "simple_weather"
)
data class SimpleWeatherEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "simpleWeatherResponse")
    val simpleWeatherResponse: SimpleWeatherResponse,
    @ColumnInfo(name = "placeId")
    val placeId: String
) : Parcelable