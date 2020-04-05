package com.srgpanov.simpleweather.ui.weather_screen


import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.entity.weather.Forecast
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.databinding.DayWeatherItemBinding
import com.srgpanov.simpleweather.databinding.MainWeatherItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import com.srgpanov.simpleweather.other.logE
import com.srgpanov.simpleweather.ui.weather_screen.WeatherAdapter.Companion.HEADER
import java.text.SimpleDateFormat
import java.util.*

sealed class WeatherHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class HeaderHolder(var binding: MainWeatherItemBinding) : WeatherHolders(binding.root) {
        var adapter: WeatherDetailAdapter
        var context:Context

        init {
            adapter = WeatherDetailAdapter()
            context=binding.root.context
        }

        fun bind(weatherRequest: WeatherResponse) {
            val fact = weatherRequest.fact
            binding.temperatureTv.text = formatTemp(fact.temp)
            binding.weatherIconIv.setImageResource(getWeatherIcon(fact.icon))
            val fellsLike =
                context.getString(R.string.feels_like) + ": " + formatTemp(fact.feels_like)
            binding.feelsLikeTv.text = fellsLike
            binding.conditionTv.text = firstLetterToUpperCase(fact.condition)
            adapter.setData(weatherRequest)
            binding.detailWeatherRv.adapter = adapter
            binding.detailWeatherRv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        private fun firstLetterToUpperCase(fact: String): String {
            val condition = fact.toCharArray()
            condition.set(0, condition[0].toUpperCase())
            return String(condition)
        }
    }

    class DaysHolder(var binding: DayWeatherItemBinding) : WeatherHolders(binding.root) {
        val context = binding.root.context
        var listener: MyClickListener? = null
        fun bind(
            forecasts: List<Forecast>,
            position: Int,
            listener: MyClickListener?
        ) {
            val nextNight = position + HEADER
            val forecast = forecasts.get(position)
            binding.dataTv.text = formatDate(forecast.date)
            binding.dayWeekTv.text = when (position) {
                0 -> context.getString(R.string.Today)
                1 -> context.getString(R.string.Tomorrow)
                else -> getDayOfWeek(forecast.date)
            }
            when (getDayOfWeekInt(forecast.date)) {
                1, 7 -> binding.dayWeekTv.setTextColor(Color.RED)
                else -> binding.dayWeekTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_text
                    )
                )
            }
            binding.tempDayTv.text = formatTemp(forecast.parts.day.temp_max)
            val nightTemp =
                when (nextNight < forecasts.size) {//показываем температуру ночи следующих суток, для последних известных суток показываем темературу текущих суток
                    true -> forecasts.get(position).parts.night.temp_min
                    false -> forecasts.get(position - HEADER).parts.night.temp_min
                }
            binding.tempNightTv.text = formatTemp(nightTemp)
            val image = forecast.parts.day.icon
            binding.weatherIconDayIv.setImageResource(getWeatherIcon(image))
            this.listener=listener
            binding.dayWeatherRoot.setOnClickListener(View.OnClickListener { view: View? ->
                listener?.onClick(view, adapterPosition)
            })
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
                return "$month $day"
            } catch (e: Exception) {
                e.logE()
                return date
            }
        }

        private fun getDayOfWeek(date: String): String {
            try {
                return when (getDayOfWeekInt(date)) {
                    1 -> context.getString(R.string.Sunday)
                    2 -> context.getString(R.string.Monday)
                    3 -> context.getString(R.string.Tuesday)
                    4 -> context.getString(R.string.Wednesday)
                    5 -> context.getString(R.string.Thursday)
                    6 -> context.getString(R.string.Friday)
                    7 -> context.getString(R.string.Saturday)
                    else -> throw IllegalStateException("Wrong day")
                }
            } catch (e: Exception) {
                e.logE()
                return date
            }
        }

        private fun getDayOfWeekInt(date: String): Int {
            try {
                val c: Calendar = Calendar.getInstance()
                val format1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dt1 = format1.parse(date)
                c.setTime(dt1)
                return c.get(Calendar.DAY_OF_WEEK)
            } catch (e: Exception) {
                e.logE()
                return 0
            }
        }
    }
}