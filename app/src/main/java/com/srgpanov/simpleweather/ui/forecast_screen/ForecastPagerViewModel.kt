package com.srgpanov.simpleweather.ui.forecast_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import javax.inject.Inject

class ForecastPagerViewModel @Inject constructor(
    position: Int,
    oneCallResponse: OneCallResponse?
) : ViewModel() {
    val oneCallResponse: MutableLiveData<OneCallResponse> = MutableLiveData<OneCallResponse>()
    var daySelected: Int = 0

    init {
        daySelected = position
        this.oneCallResponse.value = oneCallResponse
    }
}
