package com.srgpanov.simpleweather.data.models.weather

import android.location.GnssMeasurement
import android.util.Log
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.ui.App
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import com.srgpanov.simpleweather.ui.setting_screen.Temp
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

 fun format(temp: Int):String{
    val preferences = PreferenceManager.getDefaultSharedPreferences(App.instance)
    val tempMeasurement=preferences.getInt(SettingFragment.TEMP_MEASUREMENT,0)
    val tempEnum=if (tempMeasurement==0) Temp.CELSIUS else Temp.FAHRENHEIT
    var temperature: Int =temp
    if (tempEnum==Temp.FAHRENHEIT){
        temperature=((temp*1.8)+32).roundToInt()
    }
    if (temperature > 0) {
        return "+${temperature}°"
    } else {
        if (temperature < 0) {
            return "${temperature}°"
        }
        return "${temperature}°"
    }
}
internal fun getWindDirection(direction:Int):String{
    return when(direction){
       in 338..360, in 0..22->"N"
        in 23..67->"NE"
       in 68..112->"E"
       in 113..157->"SE"
       in 158..202->"S"
       in 203..247->"SW"
       in 248..292->"W"
       in  293..337->"NW"
        else -> throw IllegalStateException("Wind direction")
}}
internal fun getWindDirectionIcon(direction:Int):Int{
    return when(direction){
        in 338..360, in 0..22->R.drawable.ic_se
                in 23..67->R.drawable.ic_south
               in 68..112->R.drawable.ic_ne
               in 113..157->R.drawable.ic_east
               in 158..202->R.drawable.ic_nw
               in 203..247->R.drawable.ic_north
               in 248..292->R.drawable.ic_sw
               in  293..337->R.drawable.ic_west
        else -> throw IllegalStateException("Wind icon")
    }
}
internal fun firstLetterToUpperCase(str: String): String {
    val string = str.toCharArray()
    string.set(0, string[0].toUpperCase())
    return String(string)
}