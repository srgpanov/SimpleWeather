package com.srgpanov.simpleweather.ui.weather_screen


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Current
import com.srgpanov.simpleweather.data.models.weather.Hourly
import com.srgpanov.simpleweather.data.models.weather.Sunrise
import com.srgpanov.simpleweather.databinding.DetailWeatherItemBinding
import com.srgpanov.simpleweather.databinding.HourlyWeatherItemBinding
import com.srgpanov.simpleweather.ui.setting_screen.Pressure
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import com.srgpanov.simpleweather.ui.setting_screen.Wind
import com.srgpanov.simpleweather.ui.weather_screen.WeatherDetailAdapter.Companion.HEADER
import kotlin.math.roundToInt

sealed class WeatherDetailHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class HeaderHolder(private var binding: DetailWeatherItemBinding) :
        WeatherDetailHolders(binding.root) {
        val context: Context = binding.root.context
        private var preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        private var windMeasurement = preferences.getInt(SettingFragment.WIND_MEASUREMENT, 0)
        private var windSpeed = if (windMeasurement == 0) Wind.M_S else Wind.KM_H
        private val pressureMeasurement =
            preferences.getInt(SettingFragment.PRESSURE_MEASUREMENT, 0)
        var pressure = if (pressureMeasurement == 0) Pressure.MM_HG else Pressure.H_PA
        fun bind(current: Current) {
            val measure = if (windSpeed == Wind.M_S) {
                context.getString(R.string.m_in_s)
            } else {
                context.getString(R.string.km_h)
            }
            val windString = " ${measure}, ${current.windDirection()} "
            binding.windSpeedTextTv.text = windString
            val speedValue = if (windSpeed == Wind.M_S) {
                current.windSpeed.roundToInt().toString()
            } else {
                (current.windSpeed*3.6).roundToInt().toString()
            }
            binding.windSpeedValueTv.text = speedValue
            binding.windDirectionIconIv.setImageResource(current.windDirectionIcon())
            val pressureValue = if (pressure == Pressure.MM_HG) {
                (current.pressure*0.7501).roundToInt().toString()
            } else {
                current.pressure.toString()
            }
            val pressureMeasurement = if (pressure == Pressure.MM_HG){
                " "+context.getString(R.string.mmhg)
            }else{
                " "+context.getString(R.string.hPa)
            }
            binding.pressureValueTv.text = pressureValue
            binding.pressureTextTv.text=pressureMeasurement
            binding.humidityValueTv.text = current.humidity.toString()
        }
    }

    class HourlyHolder(private var binding: HourlyWeatherItemBinding) :
        WeatherDetailHolders(binding.root) {
        val context: Context = binding.root.context

        fun bind(list: MutableList<Any>, position: Int) {
            val context = binding.root.context
            val item = list[position]
            when (item) {
                is Sunrise -> {
                    binding.hourTimeTv.text = item.time
                    binding.hourTempTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    binding.hourTempTv.setTypeface(null, Typeface.NORMAL)
                    when (item.isSunrise) {
                        true -> {
                            binding.windIconIv.setImageResource(R.drawable.ic_sunrise)
                            binding.hourTempTv.text = context.getString(R.string.sunrise_text)
                        }
                        false -> {
                            binding.windIconIv.setImageResource(R.drawable.ic_sunset)
                            binding.hourTempTv.text = context.getString(R.string.sunset_text)
                        }
                    }
                }
                is Hourly -> {
                    binding.hourTimeTv.text =
                        when (position == HEADER) {//костылёк, чтобы для первого текущего часа выводилось "now"
                            true -> context.getString(R.string.now_text)
                            false -> {
                                when ((item.hour() == 0) and (position != HEADER)) {
                                    true -> {
                                        formatHours(item.hour()) + "\n${item.getDate()}"
                                    }
                                    false -> formatHours(item.hour())
                                }
                            }
                        }
                    binding.hourTempTv.text = item.tempFormatted()
                    binding.windIconIv.setImageResource(item.weather[0].getWeatherIcon())
                }
            }
        }

        private fun formatHours(hour: Int): String {
            return if (hour >= 10) {
                "${hour}:00"
            } else {
                "0${hour}:00"
            }
        }


    }
}