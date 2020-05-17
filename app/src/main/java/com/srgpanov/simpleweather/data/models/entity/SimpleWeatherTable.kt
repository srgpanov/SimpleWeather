package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.TimeCounter
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [
    ForeignKey(entity = PlaceTable::class,
        parentColumns = ["id"],
        childColumns = ["placeId"],
        deferred = true,
        onDelete = ForeignKey.CASCADE
    )])
data class SimpleWeatherTable (
    @PrimaryKey
    val id:String,
    val currentWeatherResponse: CurrentWeatherResponse,
    override val timeStamp:Long=System.currentTimeMillis(),
    val placeId:String
):TimeCounter, Parcelable{

}