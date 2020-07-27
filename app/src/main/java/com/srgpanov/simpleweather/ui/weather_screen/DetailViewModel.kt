package com.srgpanov.simpleweather.ui.weather_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.remote.ResponseResult.Failure
import com.srgpanov.simpleweather.data.remote.ResponseResult.Success
import com.srgpanov.simpleweather.di.ViewModelAssistedFactory
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.values
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class DetailViewModel constructor(
    var argPlace: PlaceEntity?,
    val repository: DataRepository,
    private val sharedPreferences: SharedPreferences,
    val context: Context
) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    val navEvent = NavLiveEvent()
    val weatherPlace = MutableLiveData<PlaceEntity?>()
    val currentPlace = MutableLiveData<PlaceEntity?>()
    val weatherData = MutableLiveData<WeatherState>()
    val showSetting = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<Boolean>()
    val showSnackbar = SingleLiveEvent<String>()
    val errorConnectionSnackbar = SingleLiveEvent<Boolean>()
    val requestLocationPermission = SingleLiveEvent<Unit>()

    private var isFirstStart: Boolean = false

    private val placeObserver = Observer<PlaceEntity?> { place ->
        place?.let {
            setupToolbarStatus(place)
            scope.launch {
                fetchWeather(GeoPoint(it.lat, it.lon))
            }
        }
    }

    companion object {
        const val ARGUMENT_PLACE = "ARGUMENT_PLACE"
        const val REFRESH_TIME = 43200000L // 12 hours
    }

    init {
        weatherData.value = WeatherState.EmptyWeather
        if (argPlace != null) {
            weatherPlace.value = argPlace
        }
        logD("second constructor argPlace is favorite${argPlace?.favorite} response ")
        weatherPlace.observeForever(placeObserver)
        showSetting.value = true
    }

    private fun setupToolbarStatus(place: PlaceEntity) {
        logD("setupToolbarStatus place isFavorite ${place.favorite} isCurrent ${place.current} ")
        if (place.favorite || place.current) {
            showSetting.value = true
        } else {
            scope.launch {
                val favorite = repository.placeIsFavorite(place)
                val current = repository.placeIsCurrent(place)
                logD("showSetting isFavorite $favorite isCurrent $current")
                showSetting.postValue(favorite || current)
            }
        }
    }

    override fun onCleared() {
        logD("DetailViewModel onCleared")
        weatherPlace.removeObserver(placeObserver)
        super.onCleared()
    }

    private fun loadCertainPlace() {
        scope.launch {
            val currentPlace = repository.getCurrentPlace()
            logD("current Place ${currentPlace?.title}")
            if (currentPlace == null) {
                selectCurrentPlace()
            } else {
                weatherPlace.postValue(currentPlace)
                this@DetailViewModel.currentPlace.postValue(currentPlace)
            }
        }
    }


    private fun selectCurrentPlace() {
        navEvent.postValue(
            FragmentNavEvent(
                SelectPlaceFragment::class.java
            )
        )
    }

    suspend fun fetchWeather(geoPoint: GeoPoint) {
        loadingState.postValue(true)
        val responseResult = repository.getWeather(geoPoint, false)
        loadingState.postValue(false)
        return when (responseResult) {
            is Success -> {
                weatherData.postValue(WeatherState.ActualWeather(responseResult.data))
                errorConnectionSnackbar.postValue(false)
            }
            is Failure.ServerError -> {
                val cachedWeather = repository.getCachedWeather(geoPoint)
                showSnackbar.postValue("ServerError ${responseResult.errorBody}")
                setWeather(cachedWeather)
                errorConnectionSnackbar.postValue(false)
            }
            is Failure.NetworkError -> {
                val cachedWeather = repository.getCachedWeather(geoPoint)
                setWeather(cachedWeather)
                errorConnectionSnackbar.postValue(true)
            }
        }
    }

    private fun setWeather(cachedWeather: OneCallResponse?) {
        if (cachedWeather != null) {
            val responseIsFresh = responseIsFresh(cachedWeather, REFRESH_TIME)
            if (responseIsFresh) {
                weatherData.postValue(WeatherState.ActualWeather(cachedWeather))
            } else {
                weatherData.postValue(WeatherState.ErrorWeather)
            }
        } else {
            weatherData.postValue(WeatherState.ErrorWeather)
        }
    }


    fun fetchFreshWeather() {
        scope.launch {
            val place = weatherPlace.value
            if (place != null) {
                loadingState.postValue(true)
                val responseResult = repository.getFreshWeather(place.toGeoPoint())
                loadingState.postValue(false)
                when (responseResult) {
                    is Success -> {
                        weatherData.postValue(WeatherState.ActualWeather(responseResult.data))
                        errorConnectionSnackbar.postValue(false)
                    }
                    is Failure.ServerError -> {
                        showSnackbar.postValue("ServerError ${responseResult.errorBody}")
                        errorConnectionSnackbar.postValue(false)
                    }
                    is Failure.NetworkError -> {
                        errorConnectionSnackbar.postValue(true)
                    }
                }
            } else {
                loadingState.postValue(false)
                setCurrentPlace()
            }
        }

    }

    fun changeFavoriteStatus(checked: Boolean) {
        weatherPlace.value?.let {
            scope.launch {
                if (checked) {
                    repository.saveFavoritePlace(it)
                    showSnackbar.postValue(context.getString(R.string.location_added_to_favorites))
                } else {
                    repository.removeFavoritePlace(it)
                }
            }
        }
    }

    private fun responseIsFresh(
        response: OneCallResponse,
        refreshTime: Long = DataRepository.REFRESH_TIME
    ): Boolean {
        val timeFromLastResponse = System.currentTimeMillis() - (response.current.dt * 1000L)
        logD(
            "responseIsFresh ${timeFromLastResponse < refreshTime} " + Date(
                timeFromLastResponse
            ).toString()
        )
        return timeFromLastResponse < DataRepository.REFRESH_TIME
    }


    fun onErrorConnectionClick() {
        errorConnectionSnackbar.postValue(false)
        fetchFreshWeather()
    }


    private fun locationTypeIsCurrent(): LocationType {
        val int = sharedPreferences.getInt(SettingFragment.LOCATION_TYPE, 0)
        return values()[int]
    }

    private fun firstStart(): Boolean {
        val hasVisited: Boolean = sharedPreferences.getBoolean("isFirstStart", false)
        logD("hasVisited $hasVisited")
        if (!hasVisited) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstStart", true)
            editor.apply()
            return true
        }
        return false
    }


    fun setCurrentPlace() {
        if (argPlace != null) {
            return
        }
        isFirstStart = firstStart()
        if (isFirstStart) {
            requestLocationPermission.value = Unit
        } else {
            setupWeatherPlace()
        }
    }

    private fun setupWeatherPlace() {
        logD("setupWeatherPlace ${locationTypeIsCurrent() == CURRENT}")
        if (locationTypeIsCurrent() == CURRENT) {
            setupCurrentLocation()
        } else {
            loadCertainPlace()
        }
    }

    private fun setupCurrentLocation() {
        val locationProvider = LocationProvider(CURRENT)
        scope.launch {
            val geoPoint = locationProvider.getGeoPoint()
            logD("setup Current Location $geoPoint")
            if (geoPoint != null) {
                val place = repository.getPlaceByGeoPoint(geoPoint)
                logD("setup Current ${place?.title} ${place?.oneCallResponse?.timezone_offset}")
                repository.placeIsInDb(geoPoint)
                if (place != null) {
                    place.current = true
                    repository.savePlace(place)
                    weatherPlace.postValue(place)
                    currentPlace.postValue(place)
                } else {
                    weatherData.postValue(WeatherState.ErrorWeather)
                }
            } else {
                weatherData.postValue(WeatherState.ErrorWeather)
            }
        }

    }

    private suspend fun getLocation(): GeoPoint? {
        val location = getGeoPointFromLocationManger()
        logD("getLocation lat ${location?.latitude} lon ${location?.longitude}")
        return if (location != null) {
            GeoPoint(location.latitude, location.longitude)
        } else {
            when (val geoPoint = repository.getGeoPointFromIp()) {
                is Success -> geoPoint.data.toGeoPoint()
                is Failure -> null
            }.also { logD("getLocation returned GeoPointFromIp()") }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getGeoPointFromLocationManger(): Location? {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return bestLocation
    }


    private fun locationPermissionIsGranted(): Boolean {
        val permissionCoarseLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val permissionFineLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        logD("location permissionCoarseLocation $permissionCoarseLocation permissionFineLocation $permissionFineLocation")
        return permissionCoarseLocation == PackageManager.PERMISSION_GRANTED &&
                permissionFineLocation == PackageManager.PERMISSION_GRANTED
    }

    class DetailViewModelFactory @Inject constructor(
        var repository: DataRepository,
        var preferences: SharedPreferences,
        var context: Context
    ) : ViewModelAssistedFactory<DetailViewModel> {
        override fun create(arguments: Bundle): DetailViewModel {
            val placeEntity = arguments.getParcelable<PlaceEntity?>(ARGUMENT_PLACE)
            return DetailViewModel(placeEntity, repository, preferences, context)
        }
    }


}

