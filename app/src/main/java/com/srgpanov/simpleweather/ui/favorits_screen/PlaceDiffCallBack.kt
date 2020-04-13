package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logDAnonim

class PlaceDiffCallBack(private val oldList: List<PlaceEntity>, private val newList: List<PlaceEntity>):
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        logDAnonim("areItemsTheSame oldItemPosition $oldItemPosition newItemPosition $newItemPosition")
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
        val oldPlace = oldList[oldItemPosition] as? PlaceEntity
        val newPlace = newList[newItemPosition]as? PlaceEntity
        return oldPlace?.cityTitle==newPlace?.cityTitle&&
                oldPlace?.toGeoPoint()?.pointToId()==newPlace?.toGeoPoint()?.pointToId()
    }


}