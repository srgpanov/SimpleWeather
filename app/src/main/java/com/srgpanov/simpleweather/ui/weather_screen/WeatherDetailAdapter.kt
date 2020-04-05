package com.srgpanov.simpleweather.ui.weather_screen


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.entity.weather.Fact
import com.srgpanov.simpleweather.data.entity.weather.Hour
import com.srgpanov.simpleweather.data.entity.weather.Sunrise
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.databinding.DetailWeatherItemBinding
import com.srgpanov.simpleweather.databinding.HourlyWeatherItemBinding
import com.srgpanov.simpleweather.other.logD

class WeatherDetailAdapter() : RecyclerView.Adapter<WeatherDetailHolders>() {
    var list: MutableList<Any> = ArrayList()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_HOURS = 1
        private const val TYPE_SUNSET = 2
        val HEADER: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDetailHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> WeatherDetailHolders.HeaderHolder(
                DetailWeatherItemBinding.inflate(inflater, parent, false)
            )
            TYPE_HOURS, TYPE_SUNSET -> WeatherDetailHolders.HourlyHolder(
                HourlyWeatherItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Type error")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WeatherDetailHolders, position: Int) {
        return when (holder) {
            is WeatherDetailHolders.HeaderHolder -> {
                holder.bind(list[0] as Fact)
            }
            is WeatherDetailHolders.HourlyHolder -> holder.bind(list, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Fact -> TYPE_HEADER
            is Hour -> TYPE_HOURS
            is Sunrise -> TYPE_SUNSET
            else -> throw IllegalStateException("Wrong type")
        }
    }

    fun setData(request: WeatherResponse) {
        val listFromRequest: MutableList<Any> = getDataListFromRequest(request)
        logD("request is refreshed")
        list.clear()
        list.addAll(listFromRequest)
        notifyDataSetChanged()
    }

    private fun getDataListFromRequest(request: WeatherResponse): MutableList<Any> {
        val listFromRequest: MutableList<Any> = ArrayList<Any>()
        listFromRequest.add(request.fact)
        val todaySunrise = request.forecasts[0].sunrise.take(2).toInt()
        val todaySunset = request.forecasts[0].sunset.take(2).toInt()
        val tomorrowSunrise = request.forecasts[1].sunrise.take(2).toInt()
        val tomorrowSunset = request.forecasts[1].sunset.take(2).toInt()
        for (hours in request.forecasts[0].hours) {
            if ((hours.hour >= request.getServerHour())) {
                listFromRequest.add(hours)
                if (hours.hour == todaySunrise) listFromRequest.add(
                    Sunrise(
                        request.forecasts[0].sunrise,
                        true,
                        request.forecasts[1].date
                    )
                )
                if (hours.hour == todaySunset) listFromRequest.add(
                    Sunrise(
                        request.forecasts[0].sunset,
                        false,
                        request.forecasts[1].date
                    )
                )
            }
        }
        for (hours in request.forecasts[1].hours) {
            if ((hours.hour <= request.getServerHour())) {
                listFromRequest.add(hours)
                if (hours.hour == tomorrowSunrise) listFromRequest.add(
                    Sunrise(
                        request.forecasts[1].sunrise,
                        true,
                        request.forecasts[1].date
                    )
                )
                if (hours.hour == tomorrowSunset) listFromRequest.add(
                    Sunrise(
                        request.forecasts[1].sunset,
                        false,
                        request.forecasts[1].date
                    )
                )
            }
        }
        return listFromRequest
    }
}