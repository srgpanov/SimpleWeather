package com.srgpanov.simpleweather.ui.favorits_screen

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.other.logD


class PlaceDiffCallBack(
    private val oldList: List<PlaceEntity>,
    private val newList: List<PlaceEntity>
) : DiffUtil.Callback() {
//    companion object{
//        const val TIME_STAMP = "TIME_STAMP"
//        const val TEMP = "TEMP"
//        const val ICON = "ICON"
//        const val TITLE = "TITLE"
//    }
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPlace = oldList[oldItemPosition]
        val newPlace = newList[newItemPosition]
        return oldPlace.toPlaceId() == newPlace.toPlaceId()
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPlace = oldList[oldItemPosition]
        val newPlace = newList[newItemPosition]
        logD("areContentsTheSame oldPlace timeStamp ${oldPlace.simpleWeather?.timeStamp} temp ${oldPlace.simpleWeather?.currentWeatherResponse?.main?.temp}")
        logD("areContentsTheSame newPlace timeStamp ${newPlace.simpleWeather?.timeStamp} temp ${newPlace.simpleWeather?.currentWeatherResponse?.main?.temp}")
        logD(
            "areContentsTheSame ${(oldPlace.toPlaceId() == newPlace.toPlaceId()) and
                    (oldPlace.simpleWeather?.timeStamp == newPlace.simpleWeather?.timeStamp) and
                    (oldPlace.simpleWeather?.currentWeatherResponse?.main?.temp == newPlace.simpleWeather?.currentWeatherResponse?.main?.temp)}"
        )
        return (oldPlace.toPlaceId() == newPlace.toPlaceId()) and
                (oldPlace.simpleWeather?.currentWeatherResponse?.localTime() == newPlace.simpleWeather?.currentWeatherResponse?.localTime()) and
                (oldPlace.simpleWeather?.currentWeatherResponse?.main?.temp == newPlace.simpleWeather?.currentWeatherResponse?.main?.temp)
    }

//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        val oldPlace = oldList[oldItemPosition]
//        val newPlace = newList[newItemPosition]
//
//        val diff = Bundle()
//        if (oldPlace.simpleWeather?.currentWeatherResponse?.localTime() != newPlace.simpleWeather?.currentWeatherResponse?.localTime()) {
//            val timeStamp = newPlace.simpleWeather?.currentWeatherResponse?.localTime()
//            if (timeStamp != null) diff.putString(TIME_STAMP, timeStamp)
//        }
//        if (oldPlace.simpleWeather?.currentWeatherResponse?.main?.tempFormatted() != newPlace.simpleWeather?.currentWeatherResponse?.main?.tempFormatted()) {
//            val temp = newPlace.simpleWeather?.currentWeatherResponse?.main?.tempFormatted()
//            if (temp != null) diff.putString(TEMP, temp)
//        }
//        if (oldPlace.simpleWeather?.currentWeatherResponse?.weather?.get(0)
//                ?.getWeatherIcon() != newPlace.simpleWeather?.currentWeatherResponse?.weather?.get(0)
//                ?.getWeatherIcon()
//        ) {
//            val icon =
//                newPlace.simpleWeather?.currentWeatherResponse?.weather?.get(0)?.getWeatherIcon()
//            if (icon != null) diff.getInt(ICON, icon)
//        }
//        if (oldPlace.title != newPlace.title) {
//            diff.putString(TITLE, newPlace.title)
//        }
//        return if (diff.size() == 0) {
//            null
//        } else diff
//    }
}