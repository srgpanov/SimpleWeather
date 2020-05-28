package com.srgpanov.simpleweather.data.models.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.TimeCounter
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.other.logD

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PlaceTable::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            deferred = true,
            onDelete = CASCADE
        )]
)
data class OneCallTable(
    @PrimaryKey
    val id: String,
    val oneCallResponse: OneCallResponse,
    override val timeStamp: Long = System.currentTimeMillis(),
    val placeId: String
) : TimeCounter {
    fun isFresh(refreshTime: Long = DataRepository.REFRESH_TIME): Boolean {
        logD("isFresh ${(System.currentTimeMillis() - timeStamp) < refreshTime} time ${(System.currentTimeMillis() - timeStamp) } refreshTime $refreshTime ")
        return (System.currentTimeMillis() - timeStamp) < refreshTime
    }
}