package com.srgpanov.simpleweather.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.ui.weather_screen.DetailViewModel
import com.srgpanov.simpleweather.ui.weather_widget.SettingWidgetViewModel

@Suppress("UNCHECKED_CAST")
class ArgumentsViewModelFactory<out V : ViewModel>(
    private val viewModelFactory: ViewModelAssistedFactory<V>,
    val arguments: Bundle
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                return viewModelFactory.create(arguments) as T
            }
            modelClass.isAssignableFrom(SettingWidgetViewModel::class.java) -> {
                return viewModelFactory.create(arguments) as T
            }
            else -> throw IllegalStateException("wrong ViewModel")
        }
    }
}