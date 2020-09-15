package com.srgpanov.simpleweather.ui

import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.other.SingleLiveEvent

class ShareViewModel: ViewModel(){
    val refreshWeather=SingleLiveEvent<Unit>()
}