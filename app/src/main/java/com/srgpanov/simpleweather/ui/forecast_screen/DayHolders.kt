package com.srgpanov.simpleweather.ui.forecast_screen

import android.content.Context
import android.view.View
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.ui.setting_screen.Pressure
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import com.srgpanov.simpleweather.ui.setting_screen.Wind
import kotlin.math.roundToInt

sealed class DayHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ConditionHolder(var binding: ForecastConditionItemBinding) : DayHolders(binding.root) {
        fun bind(daily: Daily) {
            binding.cloudnessTempMorningTv.text = daily.temp.mornFormated()
            binding.cloudnessTempDayTv.text =daily.temp.dayFormated()
            binding.cloudnessTempEveningTv.text =daily.temp.eveFormated()
            binding.cloudnessTempNightTv.text =daily.temp.nightFormated()
            binding.cloudnessFeelsMorningTv.text = daily.feelsLike.mornFormated()
            binding.cloudnessFeelsDayTv.text = daily.feelsLike.dayFormated()
            binding.cloudnessFeelsEveningTv.text = daily.feelsLike.eveFormated()
            binding.cloudnessFeelsNightTv.text = daily.feelsLike.nightFormated()
            binding.cloudnessIv.setImageResource(daily.weather[0].getWeatherIcon())
            binding.cloudStateTv.text=daily.weatherFormated()
        }
    }

    class WindHolder(var binding: ForecastWindItemBinding) : DayHolders(binding.root) {
        var context: Context=binding.root.context
        var preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var windMeasurement = preferences.getInt(SettingFragment.WIND_MEASUREMENT, 0)
        var windSpeed = if (windMeasurement == 0) Wind.M_S else Wind.KM_H
        fun bind(daily: Daily) {
            val measure = if (windSpeed == Wind.M_S) {
                context.getString(R.string.m_in_s)
            } else {
                context.getString(R.string.km_h)
            }
            val speedValue = if (windSpeed == Wind.M_S) {
                daily.windSpeed.roundToInt().toString()
            } else {
                (daily.windSpeed*3.6).roundToInt().toString()
            }
            binding.windSpeedMorningTv.text =
                StringBuilder("${speedValue} ${measure}")
            binding.windDirectionTv.text =daily.windDirection()
            binding.windDirectionIv.setImageResource(daily.windDirectionIcon())
        }
    }

    class HumidityHolder(var binding: ForecastHumidityItemBinding) : DayHolders(binding.root) {
        fun bind(daily: Daily) {
            val morningHumidity = "${daily.humidity}%"
            binding.humidityPercentMorningTv.text = morningHumidity

        }
    }

    class PressureHolder(var binding: ForecastPressureItemBinding) : DayHolders(binding.root) {
        val context = binding.root.context
        var preferences = PreferenceManager.getDefaultSharedPreferences(context)
        private val pressureMeasurement = preferences.getInt(SettingFragment.PRESSURE_MEASUREMENT, 0)
        var pressure=if(pressureMeasurement==0) Pressure.MM_HG else Pressure.H_PA
        fun bind(daily: Daily) {
            val pressureValue = if (pressure == Pressure.MM_HG) {
                (daily.pressure*0.7501).roundToInt().toString()
            } else {
                daily.pressure.toString()
            }
            val pressureMeasurement = if (pressure == Pressure.MM_HG){
                context.getString(R.string.mmhg)
            }else{
                context.getString(R.string.hPa)
            }
            binding.pressureValueTv.text = pressureValue
            binding.pressureScaleTv.text = pressureMeasurement

        }
    }

    class SunHolder(var binding: ForecastSunItemBinding) : DayHolders(binding.root) {
        fun bind(daily: Daily) {
            binding.sunriseTimeTv.text = daily.getSunriseString()
            binding.sunsetTimeTv.text = daily.getSunsetString()
            binding.solarDayValueTv.text = daily.dayLightHours()
        }
    }

    class MagneticHolder(var binding: ForecastMagneticItemBinding) : DayHolders(binding.root) {
        val context = binding.root.context
        fun bind(daily: Daily) {
            binding.uvIndexValueTv.text =daily.uvi.roundToInt().toString()
        }
    }
}