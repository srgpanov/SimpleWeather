package com.srgpanov.simpleweather.ui.forecast_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse

@Suppress("UNCHECKED_CAST")
class ForecastViewModelFactory(
    val position: Int,
    val oneCallResponse: OneCallResponse?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastPagerViewModel::class.java)) {
            return (ForecastPagerViewModel(position, oneCallResponse) as T)
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
