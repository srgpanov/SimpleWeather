package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import com.srgpanov.simpleweather.databinding.ForecastMagneticItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class MagneticViewItem(val uvIndex: String) : ViewItem<ForecastMagneticItemBinding> {
    override fun bind(binding: ForecastMagneticItemBinding) {
        binding.uvIndexValueTv.text = uvIndex
    }
}