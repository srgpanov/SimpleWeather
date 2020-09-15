package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.ForecastWindItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class WindViewItem(
    val speed: String,
    val direction: String,
    @DrawableRes
    val directionIcon: Int
) : ViewItem<ForecastWindItemBinding> {
    override fun bind(binding: ForecastWindItemBinding) {
        binding.windSpeedMorningTv.text = speed
        binding.windDirectionTv.text = direction
        binding.windDirectionIv.setImageResource(directionIcon)
    }
}