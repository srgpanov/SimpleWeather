package com.srgpanov.simpleweather.other

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.ui.App
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType

class LocationProvider(private val locationType: LocationType) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)
    private val repository = DataRepositoryImpl
    val context = App.instance
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager;

    companion object {
        const val LAST_KNOWN_LOCATION = "LAST_KNOWN_LOCATION"
    }


    private suspend fun loadGeoPointOfCurrentPlace(): GeoPoint? {
        val currentPlace = repository.getCurrentPlace()
        logD("current Place ${currentPlace?.title}")
        return currentPlace?.toGeoPoint()

    }

    suspend fun getGeoPoint(): GeoPoint? {
        return if (locationType == LocationType.CURRENT) {
            logD("locationTypeIsCurrent")
            return getLastKnownGeoPoint()
        } else {
            logD("locationType Is not Current")
            loadGeoPointOfCurrentPlace()
        }
    }

    private suspend fun getLastKnownGeoPoint(): GeoPoint? {
        return if (locationPermissionIsGranted()) {
            val point = getGeoPointFromLocationManger()
            if (point != null) {
                logD("getLastKnownGeoPoint point from LM != null")
                saveLastLocation(point)
                point
            }else{
                logD("getLastKnownGeoPoint point  from LM == null")
                geoPointFromIp()?:getSavedGeoPoint()
            }
        } else {
            logD("locationType current permission not granted")
            geoPointFromIp()?:getSavedGeoPoint()
        }
    }

    private suspend fun geoPointFromIp(): GeoPoint? {
        return when (val response = repository.getGeoPointFromIp()) {
            is ResponseResult.Success -> saveAndReturnGeoPoint(response.data.toGeoPoint())
            is ResponseResult.Failure -> null
        }
    }

    private fun saveAndReturnGeoPoint(point: GeoPoint): GeoPoint {
        logD("saveAndReturnGeoPoint $point")
        saveLastLocation(point)
        return point
    }


    @SuppressLint("MissingPermission")
    private fun getGeoPointFromLocationManger(): GeoPoint? {
        val providers: List<String> = locationManager.getProviders(false)
        var bestLocation: Location? = null
        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location
            }
        }
        if (bestLocation != null) {
            return GeoPoint(bestLocation.latitude, bestLocation.longitude)
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

    private fun saveLastLocation(geoPoint: GeoPoint) {
        val placeJson = Gson().toJson(geoPoint)
        sharedPreferences.edit().putString(LAST_KNOWN_LOCATION, placeJson).apply()
    }

    private fun getSavedGeoPoint(): GeoPoint? {
        val string = sharedPreferences.getString(LAST_KNOWN_LOCATION, null)
        if (string != null) {
            return Gson().fromJson(string, GeoPoint::class.java)
        } else {
            return null
        }
    }
}