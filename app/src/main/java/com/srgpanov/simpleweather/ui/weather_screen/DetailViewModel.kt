package com.srgpanov.simpleweather.ui.weather_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.RemoteDataSource
import com.srgpanov.simpleweather.data.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logDAnonim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DetailViewModel : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val remoteDataSource: RemoteDataSource = RemoteDataSourceImpl()
    val weatherData = MutableLiveData<WeatherResponse?>()
        //    private val localDataSource:LocalDataSource by lazy { LocalDataSourceImpl() }

    init {
        fetchWeather(45.035470, 38.975313)
    }
    fun fetchWeather(
        lat: Double=45.035470,
        lon: Double=38.975313
    ) {
        logD("fetchWeather")
        scope.launch(Dispatchers.IO) {


            val weather = remoteDataSource.getWeather(lat, lon)
            logD("fetchWeather $weather")
//            localDataSource.saveRequest(WeatherEntity(0,8))
            weatherData.postValue(weather)
        }
    }
    fun readDB(){
        scope.launch {
            val localDataSourceImpl=LocalDataSourceImpl()
            val favoritesPlaces = localDataSourceImpl.dao.getFavoritesPlaces()
            favoritesPlaces.forEach {
                logDAnonim(it.toString())
            }
        }
    }

}
