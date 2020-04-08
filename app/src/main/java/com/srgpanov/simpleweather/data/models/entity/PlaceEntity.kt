package com.srgpanov.simpleweather.data.models.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class PlaceEntity (
    @PrimaryKey
    val cityTitle:String,
    @Embedded
    val geoPoint: GeoPoint,
    val isCurrent:Int=0,
    val isFavorite:Int=0,
    val cityFullName:String?=""

) : Parcelable{
    fun favorite():Boolean{
        return isFavorite==1
    }
    fun current():Boolean{
        return isCurrent==1
    }

}