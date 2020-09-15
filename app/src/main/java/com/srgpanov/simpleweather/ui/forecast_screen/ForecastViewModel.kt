package com.srgpanov.simpleweather.ui.forecast_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.domain_logic.view_converters.ForecastConverter
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.CalendarItem
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.Forecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForecastViewModel constructor(
    position: Int,
    private val oneCallResponse: OneCallResponse?,
    private val forecastConverter: ForecastConverter
) : ViewModel() {
    var daySelected: Int = 0

    private val _forecastList = MutableLiveData<List<Forecast>>()
    val forecastList: LiveData<List<Forecast>>
        get() = _forecastList

    private val _calendarDay = MutableLiveData<List<CalendarItem>>()
    val calendarDay: LiveData<List<CalendarItem>>
        get() = _calendarDay


    init {
        daySelected = position
        obtainForecastList()
    }

    private fun obtainForecastList() {
        viewModelScope.launch(Dispatchers.Default) {
            val list = mutableListOf<Forecast>()
            oneCallResponse?.daily?.forEach { daily ->
                list += forecastConverter.transformForecast(daily)
            }
            _forecastList.postValue(list)
        }
        viewModelScope.launch(Dispatchers.Default) {
            val dateList: MutableList<CalendarItem> = mutableListOf()
            oneCallResponse?.daily?.forEach { daily ->
                forecastConverter.transformCalendar(daily)
                dateList += forecastConverter.transformCalendar(daily)
            }
            _calendarDay.postValue(dateList)
        }

    }
}
