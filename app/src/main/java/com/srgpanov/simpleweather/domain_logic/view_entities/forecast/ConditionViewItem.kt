package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.ForecastConditionItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class ConditionViewItem(
    val tempMorning: String,
    val tempDay: String,
    val tempEvening: String,
    val tempNight: String,
    val feelsMorning: String,
    val feelsDay: String,
    val feelsEvening: String,
    val feelsNight: String,
    @DrawableRes
    val icon: Int,
    val weatherState: String
) : ViewItem<ForecastConditionItemBinding> {
    override fun bind(binding: ForecastConditionItemBinding) {
        binding.cloudnessTempMorningTv.text = tempMorning
        binding.cloudnessTempDayTv.text = tempDay
        binding.cloudnessTempEveningTv.text = tempEvening
        binding.cloudnessTempNightTv.text = tempNight
        binding.cloudnessFeelsMorningTv.text = feelsMorning
        binding.cloudnessFeelsDayTv.text = feelsDay
        binding.cloudnessFeelsEveningTv.text = feelsEvening
        binding.cloudnessFeelsNightTv.text = feelsNight
        binding.cloudnessIv.setImageResource(icon)
        binding.cloudStateTv.text = weatherState
    }
}