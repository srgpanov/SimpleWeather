package com.srgpanov.simpleweather.ui

import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.other.SingleLiveEvent

class ShareViewModel: ViewModel(){
    val weatherPlace = SingleLiveEvent<PlaceEntity>()
    val refreshWeather=SingleLiveEvent<Unit>()
    var currentPlace=SingleLiveEvent<PlaceEntity?>()

}