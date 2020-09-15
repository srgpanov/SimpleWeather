package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.Forecast

class ForecastDayAdapter : RecyclerView.Adapter<DayHolders>() {
    lateinit var forecast: Forecast

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.forecast_condition_item -> DayHolders.ConditionHolder(
                ForecastConditionItemBinding.inflate(inflater, parent, false)
            )
            R.layout.forecast_wind_item -> DayHolders.WindHolder(
                ForecastWindItemBinding.inflate(inflater, parent, false)
            )
            R.layout.forecast_humidity_item -> DayHolders.HumidityHolder(
                ForecastHumidityItemBinding.inflate(inflater, parent, false)
            )
            R.layout.forecast_pressure_item -> DayHolders.PressureHolder(
                ForecastPressureItemBinding.inflate(inflater, parent, false)
            )
            R.layout.forecast_sun_item -> DayHolders.SunHolder(
                ForecastSunItemBinding.inflate(inflater, parent, false)
            )
            R.layout.forecast_magnetic_item -> DayHolders.MagneticHolder(
                ForecastMagneticItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Type error")
        }
    }

    override fun onBindViewHolder(holder: DayHolders, position: Int) {
        return when (holder) {
            is DayHolders.ConditionHolder -> holder.bind(forecast.condition)
            is DayHolders.WindHolder -> holder.bind(forecast.wind)
            is DayHolders.HumidityHolder -> holder.bind(forecast.humidity)
            is DayHolders.PressureHolder -> holder.bind(forecast.pressure)
            is DayHolders.SunHolder -> holder.bind(forecast.sunState)
            is DayHolders.MagneticHolder -> holder.bind(forecast.magnetic)
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.forecast_condition_item
            1 -> R.layout.forecast_wind_item
            2 -> R.layout.forecast_humidity_item
            3 -> R.layout.forecast_pressure_item
            4 -> R.layout.forecast_sun_item
            5 -> R.layout.forecast_magnetic_item
            else -> throw IllegalStateException("Wrong item type")
        }
    }

    fun setData(forecast: Forecast) {
        this.forecast = forecast
        notifyDataSetChanged()
    }
}