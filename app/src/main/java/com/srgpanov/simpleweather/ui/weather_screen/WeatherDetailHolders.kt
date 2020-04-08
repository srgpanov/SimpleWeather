package com.srgpanov.simpleweather.ui.weather_screen


import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Fact
import com.srgpanov.simpleweather.data.models.weather.Hour
import com.srgpanov.simpleweather.data.models.weather.Sunrise
import com.srgpanov.simpleweather.databinding.DetailWeatherItemBinding
import com.srgpanov.simpleweather.databinding.HourlyWeatherItemBinding
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import com.srgpanov.simpleweather.other.getWindDirectionIcon
import com.srgpanov.simpleweather.other.logE
import com.srgpanov.simpleweather.ui.weather_screen.WeatherDetailAdapter.Companion.HEADER
import java.util.*

sealed class WeatherDetailHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class HeaderHolder(private var binding: DetailWeatherItemBinding) :
        WeatherDetailHolders(binding.root) {
        val context = binding.root.context
        fun bind(fact: Fact) {
            val windString =
                " ${context.getString(R.string.m_in_s)}, ${fact.wind_dir.toUpperCase(
                    Locale.getDefault()
                )} "
            binding.windSpeedValueTv.text = fact.wind_speed.toInt().toString()
            binding.windSpeedTextTv.text = windString
            binding.windDirectionIconIv.setImageResource(getWindDirectionIcon(fact.wind_dir))
            binding.pressureValueTv.text = fact.pressure_pa.toString()
            binding.humidityValueTv.text = fact.humidity.toString()
        }
    }

    class HourlyHolder(private var binding: HourlyWeatherItemBinding) :
        WeatherDetailHolders(binding.root) {
        val context = binding.root.context
        fun bind(list: MutableList<Any>, position: Int) {
            val context = binding.root.context
            val item = list[position]
            when (item) {
                is Sunrise -> {
                    binding.hourTimeTv.text = item.time
                    binding.hourTempTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    binding.hourTempTv.setTypeface(null, Typeface.NORMAL)
                    when (item.isSunrise) {
                        true -> {
                            binding.windIconIv.setImageResource(R.drawable.ic_sunrise)
                            binding.hourTempTv.text = context.getString(R.string.sunrise_text)
                        }
                        false -> {
                            binding.windIconIv.setImageResource(R.drawable.ic_sunset)
                            binding.hourTempTv.text = context.getString(R.string.sunset_text)
                        }
                    }
                }
                is Hour -> {
                    binding.hourTimeTv.text =
                        when (position == HEADER) {//костылёк, чтобы для первого текущего часа выводилось "now"
                            true -> context.getString(R.string.now_text)
                            false -> {
                                when ((item.hour == 0) and (position != HEADER)) {
                                    true -> {
                                        var date = ""
                                        list.forEach {
                                            if (it is Sunrise) {
                                                date = formatDate(it.date)
                                                return@forEach
                                            }
                                        }
                                        formatHours(item.hour) + "\n$date"
                                    }
                                    false -> formatHours(item.hour)
                                }
                            }
                        }
                    binding.hourTempTv.text = formatTemp(item.temp)
                    binding.windIconIv.setImageResource(getWeatherIcon(item.icon))
                }
            }
        }

        private fun formatHours(hour: Int): String {
            if (hour >= 10) {
                return "${hour}:00"
            } else {
                return "0${hour}:00"
            }
        }

        private fun formatDate(date: String): String {
            try {
                var month = date.split("-")[1]
                val day = date.split("-")[2]
                when (month.toInt()) {
                    1 -> month = context.getString(R.string.January)
                    2 -> month = context.getString(R.string.February)
                    3 -> month = context.getString(R.string.March)
                    4 -> month = context.getString(R.string.April)
                    5 -> month = context.getString(R.string.May)
                    6 -> month = context.getString(R.string.June)
                    7 -> month = context.getString(R.string.July)
                    8 -> month = context.getString(R.string.August)
                    9 -> month = context.getString(R.string.September)
                    10 -> month = context.getString(R.string.October)
                    11 -> month = context.getString(R.string.November)
                    12 -> month = context.getString(R.string.December)
                }
                return "$day ${month.take(3)}"
            } catch (e: Exception) {
                e.logE()
                return date
            }
        }
    }
}