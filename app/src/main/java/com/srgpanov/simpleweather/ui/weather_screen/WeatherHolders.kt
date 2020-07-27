package com.srgpanov.simpleweather.ui.weather_screen


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class WeatherHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class HeaderHolder(var binding: MainWeatherItemBinding) : WeatherHolders(binding.root) {
        var adapter: WeatherDetailAdapter = WeatherDetailAdapter()
        var context: Context = binding.root.context

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
        val context: Context = binding.root.context
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
                binding.dayWeatherRoot.setOnClickListener { view: View? ->
                    listener?.onClick(view, bindingAdapterPosition)
                }
            }
        }

        @SuppressLint("DefaultLocale")
        private fun monthDay(date: Date): String {
            logD("locale ${Locale.getDefault().toLanguageTag()}")
            val locale = Locale.getDefault()
            val simpleDateFormat = if (locale.country == "RU") {
                SimpleDateFormat("d MMMM", locale)
            } else {
                SimpleDateFormat("MMMM d", locale)
            }
            val str = simpleDateFormat.format(date)
            return str.capitalize()
        }

        private fun getDayOfWeekInt(date: Date): Int = try {
            val c: Calendar = Calendar.getInstance()
            c.time = date
            c.get(Calendar.DAY_OF_WEEK)
        } catch (e: Exception) {
            e.logE()
            0
        }
    }
    class HeaderEmptyHolder(val binding: WeatherEmptyItemBinding) :
        WeatherHolders(binding.root)

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
        val context: Context = binding.root.context
        fun bind() {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, bindingAdapterPosition - 1)
            binding.dataTv.text = monthDay(Date(calendar.timeInMillis))
            binding.dayWeekTv.text = when (bindingAdapterPosition) {
                1 -> context.getString(R.string.Today)
                2 -> context.getString(R.string.Tomorrow)
                else -> getDayOfWeek(calendar.time)
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

        private fun monthDay(date: Date): String {
            val simpleDateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            val str = simpleDateFormat.format(date)
            val string = str.toCharArray()
            string[0] = string[0].toUpperCase()
            return String(string)

        }

    }
    protected fun getDayOfWeek(date: Date): CharSequence? {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val str = simpleDateFormat.format(date)
        val string = str.toCharArray()
        string[0] = string[0].toUpperCase()
        return String(string)
    }
}