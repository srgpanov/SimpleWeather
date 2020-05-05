package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.databinding.*

class ForecastDayAdapter() : RecyclerView.Adapter<DayHolders>() {
    lateinit var daily: Daily
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
            TYPE_MOON -> DayHolders.MagneticHolder(
                ForecastMagneticItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Type error")
        }
    }
    override fun onBindViewHolder(holder: DayHolders, position: Int) {
        return when(holder){
            is DayHolders.ConditionHolder -> holder.bind(daily)
            is DayHolders.WindHolder -> holder.bind(daily)
            is DayHolders.HumidityHolder -> holder.bind(daily)
            is DayHolders.PressureHolder -> holder.bind(daily)
            is DayHolders.SunHolder -> holder.bind(daily)
            is DayHolders.MagneticHolder -> holder.bind(daily)
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
    fun setData(daily: Daily){
        this.daily=daily
        notifyDataSetChanged()
    }

}