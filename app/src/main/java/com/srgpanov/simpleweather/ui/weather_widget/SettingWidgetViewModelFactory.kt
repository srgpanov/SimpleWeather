package com.srgpanov.simpleweather.ui.weather_widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.ui.weather_screen.DetailViewModel

class SettingWidgetViewModelFactory(val widgetId: Int) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(modelClass)) {
            return (SettingWidgetViewModel(widgetId) as T)
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}