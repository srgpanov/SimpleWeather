package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import android.util.TypedValue
import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.HourlyWeatherItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class WeatherDetailHourly(
    val hourTime: String,
    val hourTemp: String,
    val textSize: Float,
    val textTypeFace: Int,
    @DrawableRes
    val icon: Int

) : ViewItem<HourlyWeatherItemBinding> {
    override fun bind(binding: HourlyWeatherItemBinding) {
        binding.hourTimeTv.text = hourTime
        binding.hourTempTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        binding.hourTempTv.setTypeface(null, textTypeFace)
        binding.iconIv.setImageResource(icon)
        binding.hourTempTv.text = hourTemp
    }
}