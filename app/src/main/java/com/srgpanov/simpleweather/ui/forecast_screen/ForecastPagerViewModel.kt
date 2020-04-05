package com.srgpanov.simpleweather.ui.forecast_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse

class ForecastPagerViewModel : ViewModel() {
    val request: MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    var daySelected: Int=0
}
