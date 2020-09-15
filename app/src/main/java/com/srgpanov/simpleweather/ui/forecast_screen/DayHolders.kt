package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.*

sealed class DayHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ConditionHolder(var binding: ForecastConditionItemBinding) : DayHolders(binding.root) {
        fun bind(condition: ConditionViewItem) {
            condition.bind(binding)
        }
    }

    class WindHolder(var binding: ForecastWindItemBinding) : DayHolders(binding.root) {
        fun bind(wind: WindViewItem) {
            wind.bind(binding)
        }
    }

    class HumidityHolder(var binding: ForecastHumidityItemBinding) : DayHolders(binding.root) {
        fun bind(daily: HumidityViewItem) {
            daily.bind(binding)
        }
    }

    class PressureHolder(var binding: ForecastPressureItemBinding) : DayHolders(binding.root) {
        fun bind(pressure: PressureViewItem) {
            pressure.bind(binding)
        }
    }

    class SunHolder(var binding: ForecastSunItemBinding) : DayHolders(binding.root) {
        fun bind(sunState: SunViewItem) {
            sunState.bind(binding)
        }
    }

    class MagneticHolder(var binding: ForecastMagneticItemBinding) : DayHolders(binding.root) {
        fun bind(magnetic: MagneticViewItem) {
            magnetic.bind(binding)
        }
    }
}