package com.srgpanov.simpleweather.ui.weather_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.remote.ResponseResult.Failure
import com.srgpanov.simpleweather.data.remote.ResponseResult.Success
import com.srgpanov.simpleweather.other.FragmentNavEvent
import com.srgpanov.simpleweather.other.NavLiveEvent
import com.srgpanov.simpleweather.other.SingleLiveEvent
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.App
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class DetailViewModel(var argPlace: PlaceEntity?) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    private val repository = DataRepositoryImpl()
    private val context = App.instance
    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    val weatherData = MutableLiveData<OneCallResponse?>()
    val navEvent = NavLiveEvent()
    val weatherPlace = MutableLiveData<PlaceEntity?>()
    val currentPlace = MutableLiveData<PlaceEntity?>()
    val showSetting = MutableLiveData<Boolean>()
    val loadingState = MutableLiveData<Boolean>()
    val showSnackbar = SingleLiveEvent<String>()
    val showOptionsEvent = SingleLiveEvent<Boolean>()
    val errorConnectionSnackbar = SingleLiveEvent<Boolean>()
    val requestLocationPermission = SingleLiveEvent<Unit>()

    private var isFirstStart: Boolean = false


    private val placeObserver = object : Observer<PlaceEntity?> {
        override fun onChanged(place: PlaceEntity?) {
            logD("placeObserver anonymous")
            place?.let {
                setupToolbarStatus(place)
                scope.launch {
                    fetchWeather(GeoPoint(it.lat, it.lon))
                }
            }
        }

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

    init {
        weatherPlace.observeForever(placeObserver)
        showSetting.value = true
    }

    override fun onCleared() {
        logD("DetailViewModel onCleared")
        weatherPlace.removeObserver(placeObserver)
        super.onCleared()
    }

    private fun loadCurrentPlace() {
        scope.launch {
            val currentPlace = repository.getCurrentPlace()
            logD("current Place ${currentPlace?.cityTitle}")
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
                weatherData.postValue(responseResult.data)
                errorConnectionSnackbar.postValue(false)
            }
            is Failure.ServerError -> {
                val cachedWeather = repository.getCachedWeather(geoPoint)
                showSnackbar.postValue("ServerError ${responseResult.errorBody}")
                if (cachedWeather != null) {
                    val responseIsFresh = responseIsFresh(cachedWeather, 43200000);
                    if (responseIsFresh) {
                        weatherData.postValue(cachedWeather)
                    }
                }
                errorConnectionSnackbar.postValue(false)
            }
            is Failure.NetworkError -> {
                errorConnectionSnackbar.postValue(true)
            }
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
                        weatherData.postValue(responseResult.data)
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
        refreshTime: Long = DataRepositoryImpl.REFRESH_TIME
    ): Boolean {
        val timeFromLastResponse = System.currentTimeMillis() - (response.current.dt * 1000L)
        logD(
            "responseIsFresh ${timeFromLastResponse < refreshTime} " + Date(
                timeFromLastResponse
            ).toString()
        )
        return timeFromLastResponse < DataRepositoryImpl.REFRESH_TIME
    }


    fun onErrorConnectionClick() {
        errorConnectionSnackbar.postValue(false)
        fetchFreshWeather()
    }


    fun testIpToLocation() {
        scope.launch {
            repository.getPlaceFromIp()
        }
    }

    private fun locationTypeIsCurrent(): Boolean {
        return sharedPreferences.getBoolean(SettingFragment.LOCATION_TYPE_IS_CURRENT, true)
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
            weatherPlace.value = argPlace
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
        if (locationTypeIsCurrent()) {
            setupCurrentLocation()
        } else {
            loadCurrentPlace()
        }
    }

    private fun setupCurrentLocation() {
        scope.launch {
            val geoPoint = if (locationPermissionIsGranted()) {
                getLocation()
            } else {
                logD("locationType current permission not granted")
                when (val response = repository.getGeoPointFromIp()) {
                    is Success -> response.data.toGeoPoint()
                    is Failure -> null
                }
            }
            if (geoPoint != null) {
                val response = repository.getPlaceByGeoPoint(geoPoint)
                when (response) {
                    is Success -> {
                        val placeEntity = response.data.toEntity()
                        placeEntity.current = true
                        saveLastLocation(placeEntity)
                        weatherPlace.postValue(placeEntity)
                        currentPlace.postValue(placeEntity)
                    }
                    is Failure.ServerError -> {
                        val lastLocation = getLastLocation()
                        if (lastLocation != null) {
                            weatherPlace.postValue(lastLocation)
                            currentPlace.postValue(lastLocation)
                        } else {
                            weatherData.postValue(null)
                            currentPlace.postValue(null)
                        }

                    }
                    is Failure.NetworkError -> {
                        currentPlace.postValue(null)
                        weatherData.postValue(null)
                    }
                }
            } else {
                weatherData.postValue(null)
            }
        }

    }

    private fun saveLastLocation(placeEntity: PlaceEntity) {
        val placeJson = Gson().toJson(placeEntity)
        sharedPreferences.edit().putString("lastLocation", placeJson).apply()
    }

    private fun getLastLocation(): PlaceEntity? {
        val string = sharedPreferences.getString("lastLocation", null)
        if (string != null) {
            return Gson().fromJson(string, PlaceEntity::class.java)
        } else {
            return null
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation(): GeoPoint? {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        val loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        logD("getLocation lat ${loc?.latitude} lon ${loc?.longitude}")
        if (loc != null) {
            return GeoPoint(loc.latitude, loc.longitude)
        } else {
            return null
        }
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


}
