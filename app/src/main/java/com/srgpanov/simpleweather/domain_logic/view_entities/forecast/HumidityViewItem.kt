package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import com.srgpanov.simpleweather.databinding.ForecastHumidityItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

class HumidityViewItem(
    val humidity: String
) : ViewItem<ForecastHumidityItemBinding> {
    override fun bind(binding: ForecastHumidityItemBinding) {
        binding.humidityPercentMorningTv.text = humidity
    }

}