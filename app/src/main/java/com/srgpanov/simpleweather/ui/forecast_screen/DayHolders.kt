package com.srgpanov.simpleweather.ui.forecast_screen

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.Forecast
import com.srgpanov.simpleweather.databinding.*
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import com.srgpanov.simpleweather.other.getWindDirectionIcon
import com.srgpanov.simpleweather.other.logE
import java.util.*

sealed class DayHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ConditionHolder(var binding: ForecastConditionItemBinding) : DayHolders(binding.root) {
        fun bind(forecasts: Forecast) {
            binding.cloudnessTempMorningTv.text = formatTemp(forecasts.parts.morning.temp_avg)
            binding.cloudnessTempDayTv.text = formatTemp(forecasts.parts.day.temp_avg)
            binding.cloudnessTempEveningTv.text = formatTemp(forecasts.parts.evening.temp_avg)
            binding.cloudnessTempNightTv.text = formatTemp(forecasts.parts.night.temp_avg)
//
            binding.cloudnessFeelsMorningTv.text = formatTemp(forecasts.parts.morning.feels_like)
            binding.cloudnessFeelsDayTv.text = formatTemp(forecasts.parts.day.feels_like)
            binding.cloudnessFeelsEveningTv.text = formatTemp(forecasts.parts.evening.feels_like)
            binding.cloudnessFeelsNightTv.text = formatTemp(forecasts.parts.night.feels_like)
            val morningCondition = getWeatherIcon(forecasts.parts.morning.icon)
            val dayCondition = getWeatherIcon(forecasts.parts.day.icon)
            val eveningCondition = getWeatherIcon(forecasts.parts.evening.icon)
            val nightCondition = getWeatherIcon(forecasts.parts.night.icon)
            binding.cloudnessMorningIv.setImageResource(morningCondition)
            binding.cloudnessDayIv.setImageResource(dayCondition)
            binding.cloudnessEveningIv.setImageResource(eveningCondition)
            binding.cloudnessNightIv.setImageResource(nightCondition)
        }
    }

    class WindHolder(var binding: ForecastWindItemBinding) : DayHolders(binding.root) {
        var context: Context

        init {
            context = binding.root.context
        }

        fun bind(forecasts: Forecast) {
            binding.windSpeedMorningTv.text =
                StringBuilder("${forecasts.parts.morning.wind_speed.toInt()} ${context.getString(R.string.m_in_s)}")
            binding.windSpeedDayTv.text =
                StringBuilder("${forecasts.parts.day.wind_speed.toInt()} ${context.getString(R.string.m_in_s)}")
            binding.windSpeedEveningTv.text =
                StringBuilder("${forecasts.parts.evening.wind_speed.toInt()} ${context.getString(R.string.m_in_s)}")

            binding.windSpeedNightTv.text =
                StringBuilder("${forecasts.parts.night.wind_speed.toInt()} ${context.getString(R.string.m_in_s)}")
//
            binding.windSpeedUpMorningTv.text =
                StringBuilder(
                    "${context.getString(R.string.to)} ${forecasts.parts.morning.wind_gust.toInt()} ${context.getString(
                        R.string.m_in_s
                    )}"
                )
            binding.windSpeedUpDayTv.text = StringBuilder(
                "${context.getString(R.string.to)} ${forecasts.parts.day.wind_gust.toInt()} ${context.getString(
                    R.string.m_in_s
                )}"
            )
            binding.windSpeedUpEveningTv.text = StringBuilder(
                "${context.getString(R.string.to)} ${forecasts.parts.evening.wind_gust.toInt()} ${context.getString(
                    R.string.m_in_s
                )}"
            )
            binding.windSpeedUpNightTv.text = StringBuilder(
                "${context.getString(R.string.to)} ${forecasts.parts.night.wind_gust.toInt()} ${context.getString(
                    R.string.m_in_s
                )}"
            )
//
            binding.windDirectionMorningTv.text =
                forecasts.parts.morning.wind_dir.toUpperCase(Locale.getDefault())
            binding.windDirectionDayTv.text =
                forecasts.parts.day.wind_dir.toUpperCase(Locale.getDefault())
            binding.windDirectionEveningTv.text =
                forecasts.parts.evening.wind_dir.toUpperCase(Locale.getDefault())
            binding.windDirectionNightTv.text =
                forecasts.parts.night.wind_dir.toUpperCase(Locale.getDefault())
//
            binding.windDirectionMorningIv.setImageResource(
                getWindDirectionIcon(
                    forecasts.parts.morning.wind_dir
                )
            )
            binding.windDirectionDayIv.setImageResource(
                getWindDirectionIcon(
                    forecasts.parts.day.wind_dir
                )
            )
            binding.windDirectionEveningIv.setImageResource(
                getWindDirectionIcon(
                    forecasts.parts.evening.wind_dir
                )
            )
            binding.windDirectionNightIv.setImageResource(
                getWindDirectionIcon(
                    forecasts.parts.night.wind_dir
                )
            )
        }
    }

    class HumidityHolder(var binding: ForecastHumidityItemBinding) : DayHolders(binding.root) {
        fun bind(forecasts: Forecast) {
            val morningHumidity = "${forecasts.parts.morning.humidity}%"
            binding.humidityPercentMorningTv.text = morningHumidity
            val dayHumidity = "${forecasts.parts.morning.humidity}%"
            binding.humidityPercentDayTv.text = dayHumidity
            val eveningHumidity = "${forecasts.parts.evening.humidity}%"
            binding.humidityPercentEveningTv.text = eveningHumidity
            val nightHumidity = "${forecasts.parts.night.humidity}%"
            binding.humidityPercentNightTv.text = nightHumidity
        }
    }

    class PressureHolder(var binding: ForecastPressureItemBinding) : DayHolders(binding.root) {
        fun bind(forecasts: Forecast) {
            binding.pressureValueMorningTv.text = forecasts.parts.morning.pressure_pa.toString()
            binding.pressureValueDayTv.text = forecasts.parts.day.pressure_pa.toString()
            binding.pressureValueEveningTv.text = forecasts.parts.evening.pressure_pa.toString()
            binding.pressureValueNightTv.text = forecasts.parts.night.pressure_pa.toString()
        }
    }

    class SunHolder(var binding: ForecastSunItemBinding) : DayHolders(binding.root) {
        fun bind(forecasts: Forecast) {
            binding.sunriseTimeTv.text = forecasts.sunrise
            binding.sunsetTimeTv.text = forecasts.sunset
            try {
                val daylightHours =
                    forecasts.sunset.split(":")[0].toInt() - forecasts.sunrise.split(":")[0].toInt()
                val daylightMinutes =
                    forecasts.sunset.split(":")[1].toInt() - forecasts.sunrise.split(":")[1].toInt()
                val daylight = when (daylightMinutes < 0) {
                    true -> {
                        "${daylightHours - 1} h ${60 + daylightMinutes} min"
                    }
                    false -> "$daylightHours h $daylightMinutes min"
                }
                binding.solarDayValueTv.text = daylight
            } catch (e: Exception) {
                logE(e.message.toString())
            }
        }
    }

    class MoonHolder(var binding: ForecastMoonItemBinding) : DayHolders(binding.root) {
        val context = binding.root.context
        fun bind(forecasts: Forecast) {
            binding.moonStateTextTv.text = when (forecasts.moon_text) {
                "full-moon" -> context.getString(R.string.full_moon)
                "decreasing-moon" -> context.getString(R.string.decreasing_moon)
                "last-quarter" -> context.getString(R.string.last_quarter)
                "new-moon" -> context.getString(R.string.new_moon)
                "growing-moon" -> context.getString(R.string.growing_moon)
                "first-quarter" -> context.getString(R.string.first_quarter)
                else -> {
                    logE("Moon code Error")
                    ""
                }
            }
            val moonIcon = when (forecasts.moon_code) {
                0 -> R.drawable.moon_state_grow_0
                1 -> R.drawable.ic_moon_state_7
                2 -> R.drawable.ic_moon_state_6
                3 -> R.drawable.ic_moon_state_6
                4 -> R.drawable.ic_moon_state_5
                5 -> R.drawable.ic_moon_state_3
                6 -> R.drawable.ic_moon_state_2
                8 -> R.drawable.ic_new_moon
                9 -> R.drawable.moon_state_grow_7
                10 -> R.drawable.moon_state_grow_6
                11 -> R.drawable.moon_state_grow_5
                12 -> R.drawable.moon_state_grow_4
                13 -> R.drawable.moon_state_grow_3
                14 -> R.drawable.moon_state_grow_2
                15 -> R.drawable.moon_state_grow_1
                else -> {
                    logE("Error moon icon")
                    R.drawable.moon_state_grow_0
                }
            }
            binding.moonStateIconIv.setImageResource(moonIcon)
        }
    }
}