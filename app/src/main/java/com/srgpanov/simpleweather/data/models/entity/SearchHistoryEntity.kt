package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["idSearch"],
            deferred = true,
            onDelete = ForeignKey.CASCADE
        )], tableName = "search_history"
)
data class SearchHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "idSearch")
    val id: String,
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable