package com.srgpanov.simpleweather.domain_logic.view_entities.weather

import com.srgpanov.simpleweather.databinding.DetailWeatherItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class WeatherDetailCurrent(
    val windSpeed: String,
    val windSpeedValue: String,
    val windDirectionIcon: Int,
    val pressureValue: String,
    val pressureMeasurement: String,
    val humidity: String
) : ViewItem<DetailWeatherItemBinding> {
    override fun bind(binding: DetailWeatherItemBinding) {
        binding.windSpeedTextTv.text = windSpeed
        binding.windSpeedValueTv.text = windSpeedValue
        binding.windDirectionIconIv.setImageResource(windDirectionIcon)
        binding.pressureValueTv.text = pressureValue
        binding.pressureTextTv.text = pressureMeasurement
        binding.humidityValueTv.text = humidity
    }
}