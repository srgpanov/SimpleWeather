package com.srgpanov.simpleweather.domain_logic.view_converters

import android.content.Context
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.PRESSURE_MEASUREMENT
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.WIND_MEASUREMENT
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.*
import com.srgpanov.simpleweather.other.format
import com.srgpanov.simpleweather.ui.setting_screen.Pressure.H_PA
import com.srgpanov.simpleweather.ui.setting_screen.Pressure.MM_HG
import com.srgpanov.simpleweather.ui.setting_screen.Wind
import com.srgpanov.simpleweather.ui.setting_screen.Wind.KM_H
import com.srgpanov.simpleweather.ui.setting_screen.Wind.M_S
import javax.inject.Inject
import kotlin.math.roundToInt

class ForecastConverter @Inject constructor(
    private val context: Context,
    private val preferences: PreferencesStorage
) {
    @Suppress("DEPRECATION")
    fun transformCalendar(daily: Daily): CalendarItem {
        return CalendarItem(
            dayText = daily.date().format("E"),
            numberText = daily.date().date.toString(),
            isSelected = false
        )
    }

    fun transformForecast(daily: Daily): Forecast {
        return Forecast(
            condition = getCondition(daily),
            wind = getWind(daily),
            humidity = getHumidity(daily),
            pressure = getPressure(daily),
            sunState = getSunViewItem(daily),
            magnetic = getMagneticViewItem(daily)
        )
    }

    private fun getCondition(daily: Daily): ConditionViewItem {
        return ConditionViewItem(
            tempMorning = daily.temp.mornFormatted(),
            tempDay = daily.temp.dayFormatted(),
            tempEvening = daily.temp.eveFormatted(),
            tempNight = daily.temp.nightFormatted(),
            feelsMorning = daily.feelsLike.mornFormatted(),
            feelsDay = daily.feelsLike.dayFormatted(),
            feelsEvening = daily.feelsLike.eveFormatted(),
            feelsNight = daily.feelsLike.nightFormatted(),
            icon = daily.weather[0].getWeatherIcon(),
            weatherState = daily.weatherFormatted()
        )
    }

    private fun getWind(daily: Daily): WindViewItem {
        val windPref: Int by preferences(WIND_MEASUREMENT, M_S.value)
        val windMeasurement: Wind = if (windPref == 0) M_S else KM_H
        val measure = if (windMeasurement == M_S) {
            context.getString(R.string.m_in_s)
        } else {
            context.getString(R.string.km_h)
        }
        val speedConverterCoefficient = 3.6
        val speedValue = if (windMeasurement == M_S) {
            daily.windSpeed.roundToInt().toString()
        } else {
            (daily.windSpeed * speedConverterCoefficient).roundToInt().toString()
        }
        return WindViewItem(
            speed = StringBuilder("$speedValue $measure").toString(),
            direction = daily.windDirection(),
            directionIcon = daily.windDirectionIcon()
        )
    }

    private fun getHumidity(daily: Daily): HumidityViewItem {
        val morningHumidity = "${daily.humidity}%"
        return HumidityViewItem(morningHumidity)
    }

    private fun getPressure(daily: Daily): PressureViewItem {
        val pressurePref: Int by preferences(PRESSURE_MEASUREMENT, MM_HG.value)
        val pressure = if (pressurePref == MM_HG.value) MM_HG else H_PA
        val pressureConverterCoefficient = 0.7501
        val pressureValue = if (pressure == MM_HG) {
            (daily.pressure * pressureConverterCoefficient).roundToInt().toString()
        } else {
            daily.pressure.toString()
        }
        val pressureMeasurement = if (pressure == MM_HG) {
            context.getString(R.string.mmhg)
        } else {
            context.getString(R.string.hPa)
        }
        return PressureViewItem(value = pressureValue, measurement = pressureMeasurement)
    }

    private fun getSunViewItem(daily: Daily): SunViewItem {
        return SunViewItem(
            sunRise = daily.getSunriseString(),
            sunSet = daily.getSunsetString(),
            dayDuration = daily.dayLightHours(context)
        )
    }

    private fun getMagneticViewItem(daily: Daily): MagneticViewItem {
        return MagneticViewItem(uvIndex = daily.uvi.roundToInt().toString())
    }

}