package com.srgpanov.simpleweather.data.models.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            deferred = true,
            onDelete = CASCADE
        )], tableName = "one_call"
)
data class OneCallEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "oneCallResponse")
    val oneCallResponse: OneCallResponse,
    @ColumnInfo(name = "placeId")
    val placeId: String
)