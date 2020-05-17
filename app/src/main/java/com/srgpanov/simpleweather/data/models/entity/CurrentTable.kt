package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [
    ForeignKey(entity = PlaceTable::class,
        parentColumns = ["id"],
        childColumns = ["idCurrent"],
        deferred = true,
        onDelete = ForeignKey.CASCADE
    )])
data class CurrentTable(
    @PrimaryKey
    @ColumnInfo(name = "idCurrent")
    val id:String
) : Parcelable {
}