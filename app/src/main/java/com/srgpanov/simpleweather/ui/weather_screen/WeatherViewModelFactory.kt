package com.srgpanov.simpleweather.ui.weather_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity

class WeatherViewModelFactory(val place: PlaceEntity?) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return (DetailViewModel(place) as T)
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}