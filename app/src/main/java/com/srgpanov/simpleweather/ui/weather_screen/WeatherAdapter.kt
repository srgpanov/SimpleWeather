package com.srgpanov.simpleweather.ui.weather_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.DayErrorItemBinding
import com.srgpanov.simpleweather.databinding.DayWeatherItemBinding
import com.srgpanov.simpleweather.databinding.MainWeatherItemBinding
import com.srgpanov.simpleweather.databinding.WeatherErrorItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.DaysHolder
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.HeaderHolder
import kotlinx.coroutines.CoroutineScope

class WeatherAdapter() : RecyclerView.Adapter<WeatherHolders>() {
    var data: OneCallResponse?=null
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
            TYPE_EMPTY_HEADER -> WeatherHolders.HeaderErrorHolder(
                WeatherErrorItemBinding.inflate(inflater, parent, false)
            )

            TYPE_EMPTY_DAYS -> WeatherHolders.DayErrorHolder(
                DayErrorItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Type error")
        }
    }

    fun setWeather(weather: OneCallResponse?) {
        if (data!=null) {
            if (data?.current != weather?.current || data?.current != weather?.current) {
                logD("data!=weather")
                data = weather
                notifyDataSetChanged()
            } else logD("data==weather")
        }
        else {
            data = weather
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data?.daily?.size?.plus(HEADER)?:emptyAdapterSize
    }

    override fun onBindViewHolder(holder: WeatherHolders, position: Int) {
        return when (holder) {
            is HeaderHolder -> holder.bind(data, scope)
            is DaysHolder -> holder.bind(data?.daily?.get(position - HEADER), clickListener)
            is WeatherHolders.HeaderErrorHolder -> holder.bind()
            is WeatherHolders.DayErrorHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position == 0) {
            true -> {
                if (data!=null) {
                    TYPE_HEADER
                } else {
                    TYPE_EMPTY_HEADER
                }
            }
            false -> {
                if (data!=null) {
                    TYPE_DAYS
                } else {
                    TYPE_EMPTY_DAYS
                }
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = R.layout.main_weather_item
        private const val TYPE_DAYS = R.layout.day_weather_item
        private const val TYPE_EMPTY_HEADER = R.layout.weather_error_item
        private const val TYPE_EMPTY_DAYS = R.layout.day_error_item
        val HEADER: Int = 1
    }
}