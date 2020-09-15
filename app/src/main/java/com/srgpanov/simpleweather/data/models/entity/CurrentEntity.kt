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
            childColumns = ["idCurrent"],
            deferred = true,
            onDelete = ForeignKey.CASCADE
        )], tableName = "current"
)
data class CurrentEntity(
    @PrimaryKey
    @ColumnInfo(name = "idCurrent")
    val id: String
) : Parcelable