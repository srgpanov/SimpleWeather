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
import kotlinx.coroutines.*
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
    val showSetting = MutableLiveData<Boolean>()
    val showOptionsEvent = SingleLiveEvent<Boolean>()


    private val placeObserver = object : Observer<PlaceEntity?> {
        override fun onChanged(place: PlaceEntity?) {
            place?.let {
                scope.launch {
                    val favorite = repository.placeIsFavorite(place)
                    val current=repository.placeIsCurrent(place)
                    logD("showSetting isFavorite $favorite isCurrent $current")
                    showSetting.postValue(favorite||current)
                }
            }
        }

    }

    init {
        logD("init view model $place")
        if (place != null) {
            weatherPlace.value = place
            scope.launch {
                this.launch {
                    val currentPlace = repository.getCurrentPlace()
                    if (currentPlace == null) {
                        logD("cuurent place == null")

                        repository.saveCurrentPlace(place)
                    }
                }
                fetchWeather(
                    place.toGeoPoint().lat,
                    place.toGeoPoint().lon
                )
            }
        } else {
            getCurrentPlace()
        }
        weatherPlace.observeForever(placeObserver)
    }

    override fun onCleared() {
        weatherPlace.removeObserver(placeObserver)
        super.onCleared()
    }

    private fun getCurrentPlace() {
        scope.launch {
            val currentPlace = repository.getCurrentPlace()
            logD("current Place ${currentPlace?.cityTitle}")
            if (currentPlace != null) {
                fetchWeather(currentPlace.toGeoPoint().lat, currentPlace.toGeoPoint().lon)
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
                    it.toGeoPoint()
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
                if (checked){
                    repository.saveFavoritePlace(it)

                }else{
                    repository.removeFavoritePlace(it)
                }
            }
        }
    }




}
