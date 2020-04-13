package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class FavoriteTable(
    @PrimaryKey
    val id:String,
    val cityTitle: String,
    @ColumnInfo(name = "lat")
    val lat:Double,
    @ColumnInfo(name = "lon")
    val lon:Double,
    val cityFullName:String?=""
) : Parcelable {

}