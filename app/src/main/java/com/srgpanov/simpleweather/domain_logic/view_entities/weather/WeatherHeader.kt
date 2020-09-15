package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.MainWeatherItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class WeatherHeader(
    @DrawableRes
    val icon: Int,
    val temp: String,
    val tempFeels: String,
    val condition: String,
    val background: Drawable?,
    val weatherHeaderDetail: WeatherHeaderDetail
) : ViewItem<MainWeatherItemBinding> {
    override fun bind(binding: MainWeatherItemBinding) {
        binding.weatherIconIv.setImageResource(icon)
        binding.temperatureTv.text = temp
        binding.feelsLikeTv.text = tempFeels
        binding.conditionTv.text = condition
        binding.detailWeatherRv
        binding.mainWeatherRoot.background = background
    }
}