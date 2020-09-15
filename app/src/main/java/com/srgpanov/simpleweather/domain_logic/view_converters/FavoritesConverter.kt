package com.srgpanov.simpleweather.domain_logic.view_converters

import android.content.Context
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.CurrentViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.FavoritesViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import javax.inject.Inject

class FavoritesConverter @Inject constructor(
    private val context: Context
) {

    fun transformCurrent(place: PlaceViewItem?): CurrentViewItem {
        val title = place?.title ?: context.getString(R.string.current_location)
        val icon = place?.oneCallResponse?.current?.weather?.get(0)?.getWeatherIcon()
        val temp = place?.oneCallResponse?.current?.tempFormatted()
            ?: context.getString(R.string.current_place_empty_weather_temp)

        return CurrentViewItem(
            title = title,
            icon = icon,
            temp = temp,
            place = place
        )

    }

    fun transformFavorite(place: PlaceViewItem): FavoritesViewItem {
        val response = place.simpleWeather?.simpleWeatherResponse
        return FavoritesViewItem(
            title = place.title,
            background = getBackground(place),
            icon = response?.weather?.get(0)?.getWeatherIcon(),
            temp = response?.main?.tempFormatted(),
            cityTime = response?.localTime(),
            place = place
        )
    }

    private fun getBackground(place: PlaceViewItem): Int {
        return place.simpleWeather?.simpleWeatherResponse?.weather?.get(0)
            ?.getWeatherBackground() ?: R.drawable.empty_weather_background
    }
}