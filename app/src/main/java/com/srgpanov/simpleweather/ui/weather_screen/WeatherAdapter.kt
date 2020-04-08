package com.srgpanov.simpleweather.ui.weather_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse
import com.srgpanov.simpleweather.databinding.DayWeatherItemBinding
import com.srgpanov.simpleweather.databinding.MainWeatherItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.DaysHolder
import com.srgpanov.simpleweather.ui.weather_screen.WeatherHolders.HeaderHolder

class WeatherAdapter() : RecyclerView.Adapter<WeatherHolders>() {
    lateinit var data: WeatherResponse
    var clickListener:MyClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderHolder(
                MainWeatherItemBinding.inflate(inflater, parent, false)
            )
            TYPE_DAYS -> DaysHolder(
                DayWeatherItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Type error")
        }
    }

    fun setWeather(weather: WeatherResponse) {
        if (::data.isInitialized) {
            if (data.fact != weather.fact || data.forecasts!=weather.forecasts) {
                logD("data!=weather")
                data = weather
                notifyDataSetChanged()
            } else logD("data==weather")
        }else {
            data = weather
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        if (::data.isInitialized) {
            return data.forecasts.size.plus(HEADER)
        } else {
            return 0
        }
    }

    override fun onBindViewHolder(holder: WeatherHolders, position: Int) {
        return when (holder) {
            is HeaderHolder -> holder.bind(data)
            is DaysHolder -> holder.bind(data.forecasts, position - HEADER,clickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position == 0) {
            true -> TYPE_HEADER
            false -> TYPE_DAYS
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_DAYS = 1
        val HEADER: Int = 1
    }
}