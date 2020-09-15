package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.DayWeatherItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class Days(
    val data: String,
    val dayWeek: String,
    @ColorInt
    val textColor: Int,
    val tempDay: String,
    val tempNight: String,
    @DrawableRes
    val icon: Int
) : ViewItem<DayWeatherItemBinding> {

    override fun bind(binding: DayWeatherItemBinding) {
        binding.dataTv.text = data
        binding.dayWeekTv.text = dayWeek
        binding.dayWeekTv.setTextColor(textColor)
        binding.tempDayTv.text = tempDay
        binding.tempNightTv.text = tempNight
        binding.weatherIconDayIv.setImageResource(icon)
    }
}