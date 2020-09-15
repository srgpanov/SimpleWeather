package com.srgpanov.simpleweather.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.ui.forecast_screen.ForecastViewModel
import com.srgpanov.simpleweather.ui.weather_screen.DetailViewModel
import com.srgpanov.simpleweather.ui.weather_widget.SettingWidgetViewModel

@Suppress("UNCHECKED_CAST")
class ArgumentsViewModelFactory<out V : ViewModel>(
    private val viewModelFactory: ViewModelAssistedFactory<V>,
    private val arguments: Bundle
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                viewModelFactory.create(arguments) as T
            }
            modelClass.isAssignableFrom(SettingWidgetViewModel::class.java) -> {
                viewModelFactory.create(arguments) as T
            }
            modelClass.isAssignableFrom(ForecastViewModel::class.java) -> {
                viewModelFactory.create(arguments) as T
            }
            else -> throw IllegalStateException("wrong ViewModel")
        }
    }
}