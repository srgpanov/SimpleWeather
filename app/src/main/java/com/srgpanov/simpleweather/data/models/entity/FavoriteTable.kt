package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.*
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [
    ForeignKey(entity = PlaceTable::class,
        parentColumns = ["id"],
        childColumns = ["idFavorite"],
        deferred = true,
        onDelete = ForeignKey.CASCADE
    )])
data class FavoriteTable(
    @PrimaryKey
    @ColumnInfo(name = "idFavorite")
    val id:String
) : Parcelable {

}