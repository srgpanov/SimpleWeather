package com.srgpanov.simpleweather.data.entity.weather

import com.google.gson.annotations.SerializedName
import com.srgpanov.simpleweather.other.logE
import java.text.SimpleDateFormat
import java.util.*

data class Forecast(
    val date: String,
    val date_ts: Int,
    val hours: List<Hour>,
    val moon_code: Int,
    val moon_text: String,
    val parts: Parts,
    val rise_begin: String,
    val set_end: String,
    val sunrise: String,
    val sunset: String,
    val week: Int
){
    fun getDate(): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this.date)
        }catch (ex:Exception){
            logE("$ex Date error")
            Date(System.currentTimeMillis())
        }
    }
}