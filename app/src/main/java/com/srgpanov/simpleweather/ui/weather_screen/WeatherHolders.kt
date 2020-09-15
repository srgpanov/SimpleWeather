package com.srgpanov.simpleweather.ui.weather_screen


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.Days
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.WeatherHeader
import com.srgpanov.simpleweather.other.format
import com.srgpanov.simpleweather.other.getColorCompat
import java.util.*

sealed class WeatherHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {


    class HeaderWeatherHolder(var binding: MainWeatherItemBinding) : WeatherHolders(binding.root) {
        var adapter: WeatherDetailAdapter = WeatherDetailAdapter()

        fun bind(weatherHeader: WeatherHeader) {
            weatherHeader.bind(binding)
            binding.detailWeatherRv.adapter = adapter
            adapter.setData(weatherHeader.weatherHeaderDetail)
        }
    }

    class HeaderEmptyHolder(val binding: WeatherEmptyItemBinding) : WeatherHolders(binding.root)

    class HeaderErrorHolder(val binding: WeatherErrorItemBinding) : WeatherHolders(binding.root) {
        fun bind(errorActionButtonClickListener: (() -> Unit)?) {
            binding.errorActionButton.setOnClickListener {
                errorActionButtonClickListener?.invoke()
            }
        }
    }

    class DaysWeatherHolder(var binding: DayWeatherItemBinding) : WeatherHolders(binding.root) {
        fun bind(day: Days, listener: ((position: Int) -> Unit)?) {
            day.bind(binding)
            binding.dayWeatherRoot.setOnClickListener {
                listener?.invoke(bindingAdapterPosition - 1)
            }
        }
    }


    class DayErrorHolder(val binding: DayErrorItemBinding) : WeatherHolders(binding.root) {
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
                else -> binding.dayWeekTv.setTextColor(context.getColorCompat(R.color.primary_text))
            }
        }

        @SuppressLint("DefaultLocale")
        private fun monthDay(date: Date): String {
            return date.format("MMMM dd").capitalize()
        }
    }

    @SuppressLint("DefaultLocale")
    fun getDayOfWeek(date: Date): CharSequence? {
        return date.format("EEEE").capitalize()
    }
}