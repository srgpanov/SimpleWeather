package com.srgpanov.simpleweather.ui.weather_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.DaysHolder
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.HeaderHolder
import kotlinx.coroutines.CoroutineScope

class WeatherAdapter : RecyclerView.Adapter<WeatherHolders>() {
    var data: WeatherState = WeatherState.EmptyWeather
    var clickListener: MyClickListener? = null
    var scope: CoroutineScope? = null
    private val emptyAdapterSize = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderHolder(
                MainWeatherItemBinding.inflate(inflater, parent, false)
            )
            TYPE_DAYS -> DaysHolder(
                DayWeatherItemBinding.inflate(inflater, parent, false)
            )
            TYPE_ERROR_HEADER -> WeatherHolders.HeaderErrorHolder(
                WeatherErrorItemBinding.inflate(inflater, parent, false)
            )

            TYPE_EMPTY_DAYS -> WeatherHolders.DayErrorHolder(
                DayErrorItemBinding.inflate(inflater, parent, false)
            )
            TYPE_EMPTY_HEADER -> WeatherHolders.HeaderEmptyHolder(
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
            WeatherState.EmptyWeather, is WeatherState.ErrorWeather -> emptyAdapterSize
            is WeatherState.ActualWeather -> (data as WeatherState.ActualWeather).oneCallResponse.daily.size.plus(
                HEADER
            )
        }
    }

    override fun onBindViewHolder(holder: WeatherHolders, position: Int) {
        return when (holder) {
            is HeaderHolder -> {
                val weather = data as WeatherState.ActualWeather
                holder.bind(weather.oneCallResponse, scope)
            }
            is DaysHolder -> {
                val weather = data as WeatherState.ActualWeather
                holder.bind(weather.oneCallResponse.daily[position - HEADER], clickListener)
            }
            is WeatherHolders.HeaderErrorHolder -> holder.bind()
            is WeatherHolders.DayErrorHolder -> holder.bind()
            is WeatherHolders.HeaderEmptyHolder -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position == 0) {
            true -> {

                when (data) {
                    WeatherState.EmptyWeather -> TYPE_EMPTY_HEADER
                    is WeatherState.ActualWeather -> TYPE_HEADER
                    WeatherState.ErrorWeather -> TYPE_ERROR_HEADER
                }
            }
            false -> {
                when (data) {
                    is WeatherState.ActualWeather -> TYPE_DAYS
                    else -> TYPE_EMPTY_DAYS
                }
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = R.layout.main_weather_item
        private const val TYPE_EMPTY_HEADER = R.layout.weather_empty_item
        private const val TYPE_DAYS = R.layout.day_weather_item
        private const val TYPE_ERROR_HEADER = R.layout.weather_error_item
        private const val TYPE_EMPTY_DAYS = R.layout.day_error_item
        private const val HEADER: Int = 1
    }
}