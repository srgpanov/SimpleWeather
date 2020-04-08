package com.srgpanov.simpleweather.ui.weather_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.App
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DetailViewModel(place: PlaceEntity?) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    private val repository: DataRepository = DataRepositoryImpl(App.instance)

    val weatherData = MutableLiveData<WeatherResponse?>()
    val navEvent = NavLiveEvent()
    val weatherPlace = MutableLiveData<PlaceEntity?>()
    val showOptionsEvent = SingleLiveEvent<Boolean>()

    init {
        logD("init view model $place")
        if (place != null) {
            weatherPlace.value = place
            scope.launch {
                this.launch {
                    val currentPlace = repository.getCurrentPlace()
                    if (currentPlace == null) {
                        logD("cuurent place == null")
                        val newCurrentPlace = place.copy(isCurrent = 1)
                        repository.savePlace(newCurrentPlace)
                    }
                }
                fetchWeather(
                    place.geoPoint.lat,
                    place.geoPoint.lon
                )
            }
        } else {
            getCurrentPlace()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun getCurrentPlace() {
        scope.launch {
            val currentPlace = repository.getCurrentPlace()
            logD("current Place ${currentPlace?.cityTitle}")
            if (currentPlace != null) {
                fetchWeather(currentPlace.geoPoint.lat, currentPlace.geoPoint.lon)
                weatherPlace.postValue(currentPlace)

            } else {
                selectCurrentPlace()
            }
        }
    }

    private fun selectCurrentPlace() {
        navEvent.postValue(
            FragmentNavEvent(
                FavoriteFragment::class.java
            )
        )
    }

    suspend fun fetchWeather(
        lat: Double = 45.035470,
        lon: Double = 38.975313
    ) {

        val geoPoint = GeoPoint(lat, lon)
        val response = repository.getWeather(geoPoint)
        response?.let {
            weatherData.postValue(it)

        }


    }


    fun fetchFreshWeather() {
        scope.launch {
            weatherPlace.value?.let {
                val freshWeather = repository.getFreshWeather(
                    it.geoPoint
                )
                freshWeather?.let {
                    weatherData.postValue(it)
                }
            }
        }
    }

    fun changeFavoriteStatus(checked: Boolean) {
        weatherPlace.value?.let {
            scope.launch {
                logD("is favorite before ${it.isFavorite}")
                repository.changeFavotiteStatus(it)
                val place = repository.getPlace(it.geoPoint)
                weatherPlace.postValue(place)
                logD("is favorite after ${place?.isFavorite}")
            }

        }

    }


}
