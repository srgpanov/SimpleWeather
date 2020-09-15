package com.srgpanov.simpleweather.data.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.gson.Gson
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.LAST_KNOWN_LOCATION
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.LOCATION_TYPE
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.checkPermission
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class LocationProvider @Inject constructor(
    private val localDataSourceImpl: LocalDataSourceImpl,
    private val remoteDataSourceImpl: RemoteDataSourceImpl,
    preferences: PreferencesStorage,
    private val context: Context
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var lastKnownLocation: String by preferences(LAST_KNOWN_LOCATION, "")

    private var locationType: Int by preferences(LOCATION_TYPE, CURRENT.ordinal)
    private val sp = preferences.provideSharedPref()
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == LOCATION_TYPE) {
            launch { refreshCurrentLocation() }
        }
    }

    fun start() {
        launch { refreshCurrentLocation() }
        sp.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun stop() {
        sp.unregisterOnSharedPreferenceChangeListener(preferenceListener)
        this.cancel()
    }


    private suspend fun refreshCurrentLocation() {
        if (locationType == CURRENT.ordinal) {
            logD("locationTypeIsCurrent")
            refreshCurrentPlace()
        }
    }

    suspend fun refreshCurrentPlace(): PlaceViewItem? {
        val currentGeoPoint = getCurrentGeoPoint() ?: return null
        return when (val placesResult =
            remoteDataSourceImpl.getPlaces(currentGeoPoint.pointToQuery())) {
            is ResponseResult.Success -> {
                val place = placesResult.data.toPlaceItem()
                saveCurrentLocation(place)
                place
            }
            is ResponseResult.Failure -> null
        }
    }

    private suspend fun saveCurrentLocation(place: PlaceViewItem) {
        localDataSourceImpl.savePlace(place)
        localDataSourceImpl.saveCurrentPlace(place.toCurrentEntity())
    }


    suspend fun getWeatherGeoPoint(): GeoPoint? {
        return if (locationType == CURRENT.ordinal) {
            logD("locationTypeIsCurrent")
            return getCurrentGeoPoint()
        } else {
            logD("locationType Is not Current")
            getCertainGeoPoint()
        }
    }

    private suspend fun getCurrentGeoPoint(): GeoPoint? {
        return if (locationPermissionIsGranted()) {
            val point = getGeoPointFromLocationManger()
            if (point != null) {
                logD("getLastKnownGeoPoint point from LM != null")
                saveLastLocation(point)
                point
            } else {
                logD("getLastKnownGeoPoint point  from LM == null")
                geoPointFromIp() ?: getSavedGeoPoint()
            }
        } else {
            logD("locationType current permission not granted")
            geoPointFromIp() ?: getSavedGeoPoint()
        }
    }

    private suspend fun getCertainGeoPoint(): GeoPoint? {
        return localDataSourceImpl.getCurrentLocation()?.toGeoPoint()
            .also { Log.d("LocationProvider", "getCertainGeoPoint: certain Place $it") }
    }

    private suspend fun geoPointFromIp(): GeoPoint? {
        return when (val response = remoteDataSourceImpl.getGeoPointFromIp()) {
            is ResponseResult.Success -> response.data.toGeoPoint()
            is ResponseResult.Failure -> null
        }
    }


    @SuppressLint("MissingPermission")
    private fun getGeoPointFromLocationManger(): GeoPoint? {
        val providers: List<String> = locationManager.getProviders(false)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        return if (bestLocation != null) {
            GeoPoint(bestLocation.latitude, bestLocation.longitude)
        } else {
            null
        }
    }

    private fun locationPermissionIsGranted(): Boolean {
        val coarseLocationGranted = context.checkPermission(ACCESS_COARSE_LOCATION)
        val fineLocationGranted = context.checkPermission(ACCESS_FINE_LOCATION)
        return coarseLocationGranted && fineLocationGranted
    }

    private fun saveLastLocation(geoPoint: GeoPoint) {
        val placeJson = Gson().toJson(geoPoint)
        lastKnownLocation = placeJson
    }

    private fun getSavedGeoPoint(): GeoPoint? {
        return if (lastKnownLocation.isNotBlank()) {
            Gson().fromJson(lastKnownLocation, GeoPoint::class.java)
        } else {
            null
        }
    }


}