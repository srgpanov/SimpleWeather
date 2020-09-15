package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.recyclerview.widget.DiffUtil
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.FavoritesViewItem


class PlaceDiffCallBack(
    private val oldList: List<FavoritesViewItem>,
    private val newList: List<FavoritesViewItem>
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
    return oldPlace.place.toPlaceId() == newPlace.place.toPlaceId()
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

        return (oldPlace.background == newPlace.background) and
                (oldPlace.cityTime == newPlace.cityTime) and
                (oldPlace.temp == newPlace.temp) and
                (oldPlace.title == newPlace.title) and
                (oldPlace.icon == newPlace.icon)
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldPlace = oldList[oldItemPosition]
        val newPlace = newList[newItemPosition]
        val payloads = oldPlace.getPayloads(newPlace)
        return if (payloads.isEmpty()) {
            null
        } else {
            payloads
        }

    }
}