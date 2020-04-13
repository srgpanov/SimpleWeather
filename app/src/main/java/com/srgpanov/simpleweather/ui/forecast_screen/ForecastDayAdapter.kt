package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.databinding.*

class ForecastDayAdapter() : RecyclerView.Adapter<DayHolders>() {
    lateinit var forecasts: TwoDayForecast
    companion object {
        private const val TYPE_CONDITION = 0
        private const val TYPE_WIND = 1
        private const val TYPE_HUMIDITY = 2
        private const val TYPE_PRESSURE = 3
        private const val TYPE_SUN = 4
        private const val TYPE_MOON = 5
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolders {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CONDITION -> DayHolders.ConditionHolder(
                ForecastConditionItemBinding.inflate(inflater, parent, false)
            )
            TYPE_WIND -> DayHolders.WindHolder(
                ForecastWindItemBinding.inflate(inflater, parent, false)
            )
            TYPE_HUMIDITY -> DayHolders.HumidityHolder(
                ForecastHumidityItemBinding.inflate(inflater, parent, false)
            )
            TYPE_PRESSURE -> DayHolders.PressureHolder(
                ForecastPressureItemBinding.inflate(inflater, parent, false)
            )
            TYPE_SUN -> DayHolders.SunHolder(
                ForecastSunItemBinding.inflate(inflater, parent, false)
            )
            TYPE_MOON -> DayHolders.MoonHolder(
                ForecastMoonItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Type error")
        }
    }
    override fun onBindViewHolder(holder: DayHolders, position: Int) {
        return when(holder){
            is DayHolders.ConditionHolder -> holder.bind(forecasts)
            is DayHolders.WindHolder -> holder.bind(forecasts)
            is DayHolders.HumidityHolder -> holder.bind(forecasts)
            is DayHolders.PressureHolder -> holder.bind(forecasts)
            is DayHolders.SunHolder -> holder.bind(forecasts.todayForecast)
            is DayHolders.MoonHolder -> holder.bind(forecasts.todayForecast)
        }
    }
    override fun getItemCount(): Int {
        return 6
    }
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0-> TYPE_CONDITION
            1-> TYPE_WIND
            2-> TYPE_HUMIDITY
            3-> TYPE_PRESSURE
            4-> TYPE_SUN
            5-> TYPE_MOON
            else -> throw IllegalStateException("Wrong item type")
        }
    }
    fun setData(forecasts: TwoDayForecast){
        this.forecasts=forecasts
        notifyDataSetChanged()
    }

}