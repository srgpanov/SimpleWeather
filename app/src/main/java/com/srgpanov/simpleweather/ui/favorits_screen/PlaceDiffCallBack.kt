package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.other.logD

class PlaceDiffCallBack(private val oldList: List<PlaceEntity>, private val newList: List<PlaceEntity>):
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPlace = oldList[oldItemPosition]
        val newPlace = newList[newItemPosition]
        return  oldPlace.toGeoPoint().pointToId() == newPlace.toGeoPoint().pointToId()
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
        logD("areContentsTheSame ${oldPlace.toGeoPoint().pointToId() == newPlace.toGeoPoint().pointToId()&&
                oldPlace.simpleWeatherTable?.time==newPlace.simpleWeatherTable?.time}")
        return oldPlace.toGeoPoint().pointToId() == newPlace.toGeoPoint().pointToId()
                &&
                oldPlace.simpleWeatherTable?.time==newPlace.simpleWeatherTable?.time
                &&
                oldPlace.simpleWeatherTable?.currentWeatherResponse?.main?.temp==newPlace.simpleWeatherTable?.currentWeatherResponse?.main?.temp
    }


}