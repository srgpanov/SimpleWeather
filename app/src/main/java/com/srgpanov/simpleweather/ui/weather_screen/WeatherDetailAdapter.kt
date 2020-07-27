package com.srgpanov.simpleweather.ui.weather_screen


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.models.weather.Current
import com.srgpanov.simpleweather.data.models.weather.Hourly
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.models.weather.Sunrise
import com.srgpanov.simpleweather.databinding.DetailWeatherItemBinding
import com.srgpanov.simpleweather.databinding.HourlyWeatherItemBinding
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherDetailAdapter : RecyclerView.Adapter<WeatherDetailHolders>() {
    var list: MutableList<Any> = ArrayList()
    val scope: CoroutineScope? = null

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_HOURS = 1
        private const val TYPE_SUNSET = 2
        const val HEADER: Int = 1
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
                holder.bind(list[0] as Current)
            }
            is WeatherDetailHolders.HourlyHolder -> holder.bind(list, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is Current -> TYPE_HEADER
            is Hourly -> TYPE_HOURS
            is Sunrise -> TYPE_SUNSET
            else -> throw IllegalStateException("Wrong type")
        }
    }

    suspend fun setData(request: OneCallResponse) {
        withContext(Dispatchers.Default) {
            val listFromRequest: MutableList<Any> = getDataListFromRequest(request)
            list.clear()
            list.addAll(listFromRequest)
        }
    }


private fun getDataListFromRequest(request: OneCallResponse): MutableList<Any> {
    val listFromRequest: MutableList<Any> = mutableListOf()
    request.setOffsets()
    listFromRequest.add(request.current)
    for (hours in request.hourly) {
        listFromRequest.add(hours)
        for (daily in request.daily) {
//            logD("getHourSunrise  getDay ${daily.getDay()} day ${  hours.day()}" )
            if (daily.getDay() == hours.day()) {
                logD("getHourSunrise hours.hour() ${hours.hour() } daily.getHourSunrise() ${daily.getHourSunrise()}")
                val hourOfSunrise = hours.hour() == daily.getHourSunrise()
                val hourOfSunset = hours.hour() == daily.getHourSunset()
                if (hourOfSunrise) listFromRequest.add(
                    Sunrise(
                        daily.getSunriseString(),
                        true
                    )
                )
                if (hourOfSunset) listFromRequest.add(
                    Sunrise(
                        daily.getSunsetString(),
                        false
                    )
                )
            }
        }
    }

    return listFromRequest
}
}