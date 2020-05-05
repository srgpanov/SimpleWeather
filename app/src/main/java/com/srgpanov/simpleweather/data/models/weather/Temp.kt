package com.srgpanov.simpleweather.data.models.weather


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.other.logD
import kotlinx.android.parcel.Parcelize
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

@Parcelize
data class Temp(
    @SerializedName("day")
    val day: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("max")
    val max: Double,
    @SerializedName("min")
    val min: Double,
    @SerializedName("morn")
    val morn: Double,
    @SerializedName("night")
    val night: Double
) : Parcelable {
    fun dayFormated():String{
        val tempInt =day.roundToInt()
        return format(tempInt)
    }
    fun eveFormated():String{
        val tempInt =eve.roundToInt()
        return format(tempInt)
    }
    fun mornFormated():String{
        val tempInt =morn.roundToInt()
        return format(tempInt)
    }
    fun nightFormated():String{
        val tempInt =night.roundToInt()
        return format(tempInt)
    }
    fun minFormated():String{
        val tempInt =min.roundToInt()
        return format(tempInt)
    }
    fun maxFormated():String{
        val tempInt =max.roundToInt()
        return format(tempInt)
    }




}