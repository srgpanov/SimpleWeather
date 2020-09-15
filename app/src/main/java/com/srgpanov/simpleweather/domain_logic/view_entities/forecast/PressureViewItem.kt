package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import com.srgpanov.simpleweather.databinding.ForecastPressureItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class PressureViewItem(
    val value: String,
    val measurement: String
) : ViewItem<ForecastPressureItemBinding> {
    override fun bind(binding: ForecastPressureItemBinding) {
        binding.pressureValueTv.text = value
        binding.pressureScaleTv.text = measurement
    }
}