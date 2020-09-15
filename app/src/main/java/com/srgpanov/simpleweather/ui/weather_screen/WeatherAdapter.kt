package com.srgpanov.simpleweather.ui.weather_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.ui.weather_screen.WeatherState.*

class WeatherAdapter : RecyclerView.Adapter<WeatherHolders>() {
    var data: WeatherState = EmptyWeather
    var clickListener: ((position: Int) -> Unit)? = null
    var errorClickListener: (() -> Unit)? = null
    private val emptyAdapterSize = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.main_weather_item -> WeatherHolders.HeaderWeatherHolder(
                MainWeatherItemBinding.inflate(inflater, parent, false)
            )
            R.layout.day_weather_item -> WeatherHolders.DaysWeatherHolder(
                DayWeatherItemBinding.inflate(inflater, parent, false)
            )
            R.layout.weather_error_item -> WeatherHolders.HeaderErrorHolder(
                WeatherErrorItemBinding.inflate(inflater, parent, false)
            )

            R.layout.day_error_item -> WeatherHolders.DayErrorHolder(
                DayErrorItemBinding.inflate(inflater, parent, false)
            )
            R.layout.weather_empty_item -> WeatherHolders.HeaderEmptyHolder(
                WeatherEmptyItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Type error")
        }
    }

    fun setWeather(weather: WeatherState) {
        data = weather
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return when (data) {
            EmptyWeather, is ErrorWeather -> emptyAdapterSize
            is ActualWeather -> (data as ActualWeather).weatherViewItem.dayList.size.plus(HEADER)
        }
    }

    override fun onBindViewHolder(holder: WeatherHolders, position: Int) {
        return when (holder) {
            is WeatherHolders.HeaderWeatherHolder -> {
                val weather = data as ActualWeather
                holder.bind(weather.weatherViewItem.header)
            }
            is WeatherHolders.DaysWeatherHolder -> {
                val weather = data as ActualWeather
                holder.bind(weather.weatherViewItem.dayList[position - HEADER], clickListener)
            }
            is WeatherHolders.HeaderErrorHolder -> holder.bind(errorClickListener)
            is WeatherHolders.DayErrorHolder -> holder.bind()
            is WeatherHolders.HeaderEmptyHolder -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position == 0) {
            true -> {
                when (data) {
                    EmptyWeather -> R.layout.weather_empty_item
                    is ActualWeather -> R.layout.main_weather_item
                    ErrorWeather -> R.layout.weather_error_item
                }
            }
            false -> {
                when (data) {
                    is ActualWeather -> R.layout.day_weather_item
                    else -> R.layout.day_error_item
                }
            }
        }
    }

    companion object {
        private const val HEADER: Int = 1
    }
}