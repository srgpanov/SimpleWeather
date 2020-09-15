package com.srgpanov.simpleweather.domain_logic.view_converters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.PRESSURE_MEASUREMENT
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.WIND_MEASUREMENT
import com.srgpanov.simpleweather.data.models.weather.*
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.*
import com.srgpanov.simpleweather.other.format
import com.srgpanov.simpleweather.other.getColorCompat
import com.srgpanov.simpleweather.other.getDrawableCompat
import com.srgpanov.simpleweather.ui.setting_screen.Pressure
import com.srgpanov.simpleweather.ui.setting_screen.Wind
import com.srgpanov.simpleweather.ui.weather_screen.WeatherDetailAdapter
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class DetailConverter @Inject constructor(val context: Context) {
    private val preferences: PreferencesStorage = PreferencesStorage(context)

    private var windMeasurement: Int by preferences(WIND_MEASUREMENT, Wind.M_S.value)
    private val windSpeed: Wind
        get() = if (windMeasurement == 0) Wind.M_S else Wind.KM_H
    private val pressureMeasurement: Int by preferences(PRESSURE_MEASUREMENT, Pressure.MM_HG.value)
    private val pressure: Pressure
        get() = if (pressureMeasurement == 0) Pressure.MM_HG else Pressure.H_PA

    fun transform(oneCallResponse: OneCallResponse): WeatherViewItem {
        return WeatherViewItem(
            oneCallResponse = oneCallResponse,
            header = getWeatherHeader(oneCallResponse),
            dayList = getDayList(oneCallResponse)
        )
    }

    private fun getWeatherHeader(oneCallResponse: OneCallResponse): WeatherHeader {
        val weatherBackground = oneCallResponse.current.weather[0].getWeatherBackground()
        val fellsLike =
            context.getString(R.string.feels_like) + ": " + oneCallResponse.current.feelsLikeFormatted()
        val detailCurrent = getWeatherDetailCurrent(oneCallResponse.current)
        val detailHourly = getWeatherDetailHourly(oneCallResponse)
        val headerDetail =
            WeatherHeaderDetail(detailCurrent, detailHourly)
        return WeatherHeader(
            icon = oneCallResponse.current.weather[0].getWeatherIcon(),
            temp = oneCallResponse.current.tempFormatted(),
            background = context.getDrawableCompat(weatherBackground),
            condition = oneCallResponse.current.weatherFormatted(),
            tempFeels = fellsLike,
            weatherHeaderDetail = headerDetail
        )
    }

    private fun getWeatherDetailCurrent(current: Current): WeatherDetailCurrent {
        val windString = getWindString(current)
        val pressureMeasurement = getPressureMeasurement()
        val pressureValue = getPressureValue(current)
        val speedValue = getSpeedValue(current)
        return WeatherDetailCurrent(
            windSpeed = windString,
            windSpeedValue = speedValue,
            windDirectionIcon = current.windDirectionIcon(),
            pressureValue = pressureValue,
            pressureMeasurement = pressureMeasurement,
            humidity = current.humidity.toString()
        )
    }

    private fun getWindString(current: Current): String {
        val measure = if (windSpeed == Wind.M_S) {
            context.getString(R.string.m_in_s)
        } else {
            context.getString(R.string.km_h)
        }
        return " ${measure}, ${current.windDirection()} "
    }

    private fun getSpeedValue(current: Current): String {
        return if (windSpeed == Wind.M_S) {
            current.windSpeed.roundToInt().toString()
        } else {
            (current.windSpeed * 3.6).roundToInt().toString()
        }
    }

    private fun getPressureValue(current: Current): String {
        val coefficient = 0.7501
        return if (pressure == Pressure.MM_HG) {
            (current.pressure * coefficient).roundToInt().toString()
        } else {
            current.pressure.toString()
        }
    }

    private fun getPressureMeasurement(): String {
        return if (pressure == Pressure.MM_HG) {
            " " + context.getString(R.string.mmhg)
        } else {
            " " + context.getString(R.string.hPa)
        }
    }

    private fun getWeatherDetailHourly(oneCallResponse: OneCallResponse): List<WeatherDetailHourly> {
        val data: MutableList<Any> = getDataListFromRequest(oneCallResponse)
        val hourlyViewItemList = mutableListOf<WeatherDetailHourly>()
        for ((index, item) in data.withIndex()) {
            val weatherDetailHourly = when (item) {
                is SunState -> getHourlyFromSunState(item)
                is Hourly -> getHourlyFromHours(item, index)
                is Current -> continue
                else -> throw IllegalStateException("error on parsing hourly weather $item")
            }
            hourlyViewItemList.add(weatherDetailHourly)
        }
        return hourlyViewItemList
    }

    private fun getHourlyFromSunState(item: SunState): WeatherDetailHourly {
        val sunStateTextSize = 14f
        return WeatherDetailHourly(
            hourTime = item.time,
            hourTemp = getSunStateText(item),
            icon = getSunStateIcon(item),
            textSize = sunStateTextSize,
            textTypeFace = Typeface.NORMAL
        )
    }

    private fun getSunStateText(item: SunState): String {
        return when (item) {
            is SunState.Sunrise -> context.getString(R.string.sunrise_text)
            is SunState.Sunset -> context.getString(R.string.sunset_text)
        }
    }

    private fun getSunStateIcon(item: SunState): Int {
        return when (item) {
            is SunState.Sunrise -> R.drawable.ic_sunrise
            is SunState.Sunset -> R.drawable.ic_sunset
        }
    }

    private fun getHourlyFromHours(
        item: Hourly,
        index: Int
    ): WeatherDetailHourly {
        val hourlyTextSize = 16f
        return WeatherDetailHourly(
            hourTime = getHourString(item, index),
            hourTemp = item.tempFormatted(),
            icon = item.weather[0].getWeatherIcon(),
            textSize = hourlyTextSize,
            textTypeFace = Typeface.BOLD
        )
    }

    private fun getHourString(item: Hourly, index: Int): String {
        val isCurrentHour = index == WeatherDetailAdapter.HEADER
        return if (isCurrentHour) {
            context.getString(R.string.now_text)
        } else {
            val isMidnightHour = item.hour() == 0
            if (!isCurrentHour and isMidnightHour) {
                formatHours(item.localTime) + "\n${getDate(item.localTime)}"
            } else {
                formatHours(item.localTime)
            }
        }
    }

    private fun formatHours(time: Long): String {
        return Date(time).format("HH:mm")
    }

    fun getDate(time: Long): String {
        return Date(time).format("d MMM")
    }

    private fun getDataListFromRequest(request: OneCallResponse): MutableList<Any> {
        val listFromRequest: MutableList<Any> = mutableListOf()
        request.setOffsets()
        listFromRequest.add(request.current)
        for (hours in request.hourly) {
            listFromRequest.add(hours)
            for (daily in request.daily) {
                if (daily.getDay() == hours.day()) {
                    val hourOfSunrise = hours.hour() == daily.getHourSunrise()
                    val hourOfSunset = hours.hour() == daily.getHourSunset()
                    if (hourOfSunrise)
                        listFromRequest.add(SunState.Sunrise(daily.getSunriseString()))
                    if (hourOfSunset)
                        listFromRequest.add(SunState.Sunset(daily.getSunsetString()))
                }
            }
        }
        return listFromRequest
    }

    private fun getDayList(oneCallResponse: OneCallResponse): List<Days> {
        val dayList = mutableListOf<Days>()
        for ((index, item) in oneCallResponse.daily.withIndex()) {
            val day = Days(
                data = monthDay(item.date()),
                icon = item.weather[0].getWeatherIcon(),
                dayWeek = getDayOFWeek(item, index),
                tempDay = item.temp.dayFormatted(),
                tempNight = item.temp.nightFormatted(),
                textColor = getTextColor(item)
            )
            dayList.add(day)
        }
        return dayList
    }

    @SuppressLint("DefaultLocale")
    private fun monthDay(date: Date): String {
        val locale = Locale.getDefault()
        val pattern = if (locale.country == "RU") "d MMMM" else "MMMM d"
        return date.format(pattern).capitalize()
    }


    private fun getDayOFWeek(item: Daily, position: Int): String {
        return when (position) {
            0 -> context.getString(R.string.Today)
            1 -> context.getString(R.string.Tomorrow)
            else -> getDayOfWeek(item.date())
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getDayOfWeek(date: Date): String {
        return date.format("EEEE").capitalize()
    }

    private fun getTextColor(daily: Daily): Int {
        return when (getDayOfWeekInt(daily.date())) {
            1, 7 -> Color.RED
            else -> context.getColorCompat(R.color.primary_text)
        }
    }

    private fun getDayOfWeekInt(date: Date): Int {
        val c: Calendar = Calendar.getInstance()
        c.time = date
        return c.get(Calendar.DAY_OF_WEEK)
    }
}