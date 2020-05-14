package com.srgpanov.simpleweather.ui.weather_screen


import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.DayErrorItemBinding
import com.srgpanov.simpleweather.databinding.DayWeatherItemBinding
import com.srgpanov.simpleweather.databinding.MainWeatherItemBinding
import com.srgpanov.simpleweather.databinding.WeatherErrorItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class WeatherHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class HeaderHolder(var binding: MainWeatherItemBinding) : WeatherHolders(binding.root) {
        var adapter: WeatherDetailAdapter
        var context: Context

        init {
            adapter = WeatherDetailAdapter()
            context = binding.root.context
        }

        fun bind(
            weatherRequest: OneCallResponse?,
            scope: CoroutineScope?
        ) {
            if (weatherRequest != null) {
                val current = weatherRequest.current
                binding.weatherIconIv.setImageResource(current.weather[0].getWeatherIcon())
                binding.temperatureTv.text = current.tempFormatted()
                val fellsLike =
                    context.getString(R.string.feels_like) + ": " + current.feelsLikeFormatted()
                binding.feelsLikeTv.text = fellsLike
                binding.conditionTv.text = current.weatherFormatted()
                binding.detailWeatherRv.adapter = adapter

                binding.mainWeatherRoot.background =
                    context.getDrawable(current.weather[0].getWeatherBackground())
                scope?.launch(Dispatchers.Main) {
                    adapter.setData(weatherRequest)
                    if (isActive) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    class DaysHolder(var binding: DayWeatherItemBinding) : WeatherHolders(binding.root) {
        val context = binding.root.context
        var listener: MyClickListener? = null
        fun bind(daily: Daily?, listener: MyClickListener?) {
            if (daily != null) {
                binding.dataTv.text = monthDay(daily.date())
                binding.dayWeekTv.text = when (bindingAdapterPosition) {
                    1 -> context.getString(R.string.Today)
                    2 -> context.getString(R.string.Tomorrow)
                    else -> getDayOfWeek(daily.date())
                }
                when (getDayOfWeekInt(daily.date())) {
                    1, 7 -> binding.dayWeekTv.setTextColor(Color.RED)
                    else -> binding.dayWeekTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.primary_text
                        )
                    )
                }
                binding.tempDayTv.text = daily.temp.dayFormated()
                binding.tempNightTv.text = daily.temp.nightFormated()
                binding.weatherIconDayIv.setImageResource(daily.weather[0].getWeatherIcon())
                this.listener = listener
                binding.dayWeatherRoot.setOnClickListener(View.OnClickListener { view: View? ->
                    listener?.onClick(view, adapterPosition)
                })
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
                return "$month $day"
            } catch (e: Exception) {
                e.logE()
                return date
            }
        }

        private fun monthDay(date: Date): String {
            val simpleDateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            val str = simpleDateFormat.format(date)
            val string = str.toCharArray()
            string.set(0, string[0].toUpperCase())
            return String(string)

        }

        private fun getDayOfWeek(date: Date): String {
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
                return date.day.toString()
            }
        }

        private fun getDayOfWeekInt(date: Date): Int {
            try {
                val c: Calendar = Calendar.getInstance()
                c.setTime(date)
                return c.get(Calendar.DAY_OF_WEEK)
            } catch (e: Exception) {
                e.logE()
                return 0
            }
        }
    }

    class HeaderErrorHolder(val binding: WeatherErrorItemBinding) :
        WeatherHolders(binding.root) {
        fun bind() {
            binding.errorActionButton.setOnClickListener {
                //todo
                //listener?.onClick(it, bindingAdapterPosition)
            }
        }


    }

    class DayErrorHolder(val binding: DayErrorItemBinding) :
        WeatherHolders(binding.root) {
        val context = binding.root.context
        fun bind() {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, bindingAdapterPosition - 1)
            binding.dataTv.text = monthDay(Date(calendar.timeInMillis))
            binding.dayWeekTv.text = when (bindingAdapterPosition) {
                1 -> context.getString(R.string.Today)
                2 -> context.getString(R.string.Tomorrow)
                else -> getDayOfWeek(calendar.timeInMillis)
            }
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                1, 7 -> binding.dayWeekTv.setTextColor(Color.RED)
                else -> binding.dayWeekTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_text
                    )
                )
            }
        }

        private fun getDayOfWeek(time: Long): CharSequence? {
            val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

            val str = simpleDateFormat.format(Date(time))
            val string = str.toCharArray()
            string.set(0, string[0].toUpperCase())
            return String(string)
        }

        private fun monthDay(date: Date): String {
            val simpleDateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            val str = simpleDateFormat.format(date)
            val string = str.toCharArray()
            string.set(0, string[0].toUpperCase())
            return String(string)

        }

    }
}