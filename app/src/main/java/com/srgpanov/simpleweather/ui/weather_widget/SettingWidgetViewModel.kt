package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.di.ViewModelAssistedFactory
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingWidgetViewModel(
    val widgetId: Int,
    var repository: DataRepository,
    var context: Context
) : ViewModel() {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private lateinit var locationProvider: LocationProvider
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val locationLatitude: Double = getCoordinate(WIDGET_LATITUDE + widgetId)
    private val locationLongitude: Double = getCoordinate(WIDGET_LONGITUDE + widgetId)
    val mutableIsLightTheme = MutableLiveData<Boolean>()
    val mutableTimeOfLastUpdate = MutableLiveData<Boolean>()
    val mutableTransparency = MutableLiveData<Int>()
    val mutableLocationType = MutableLiveData<LocationType>()
    val mutableLocationDescription = MutableLiveData<String>()
    val mutableWidgetPlace = MutableLiveData<PlaceEntity>()
    val isLightTheme: LiveData<Boolean> = mutableIsLightTheme
    val timeOfLastUpdate: LiveData<Boolean> = mutableTimeOfLastUpdate
    val transparency: LiveData<Int> = mutableTransparency
    val locationType: LiveData<LocationType> = mutableLocationType
    val widgetPlace: LiveData<PlaceEntity> = mutableWidgetPlace

    companion object {
        const val ALPHA_MAX_VALUE = 255
        const val ARGUMENT_WIDGET = "ARGUMENT_WIDGET"
    }

    init {
        restoreSettings()
        logD("widget Id $widgetId")
    }

    private fun restoreSettings() {
        val icons = preferences.getBoolean(WIDGET_ICONS + widgetId, false)
        val lightTheme = preferences.getBoolean(WIDGET_LIGHT_THEME + widgetId, false)
        val timeOfUpdate = preferences.getBoolean(WIDGET_SHOW_TIME_UPDATE + widgetId, false)
        val transparency = preferences.getInt(WIDGET_TRANSPARENCY + widgetId, ALPHA_MAX_VALUE)
        val locationType = getLocationType()
        locationProvider = LocationProvider(CURRENT)
        mutableIsLightTheme.value = lightTheme
        mutableTimeOfLastUpdate.value = timeOfUpdate
        mutableTransparency.value = transparency
        mutableLocationType.value = locationType
        scope.launch {
            when (locationType) {
                CURRENT -> restoreCurrentLocation()
                CERTAIN -> restoreCertainLocation()
            }
        }

    }

    private suspend fun restoreCurrentLocation() {
        val geoPoint = locationProvider.getGeoPoint()
        val place =
            repository.getPlaceByGeoPoint(geoPoint ?: GeoPoint().also { logE("GeoPoint null") })
        if (place != null) {
            mutableWidgetPlace.postValue(place)
        } else {
            logE("restoreCurrentLocation place==nul")
        }
    }

    private suspend fun restoreCertainLocation() {
        val place = repository.getPlaceByGeoPoint(GeoPoint(locationLatitude, locationLongitude))
        if (place != null) {
            mutableWidgetPlace.postValue(place)
            mutableLocationDescription.postValue(place.title)
        }
    }

    private fun getLocationType(): LocationType {
        val savedType =
            preferences.getInt(
                WIDGET_LOCATION_TYPE + widgetId,
                CURRENT.ordinal
            )
        return values()[savedType]
    }

    private fun getCoordinate(key: String): Double {
        return try {
            preferences.getString(key, "")?.toDouble()!!
        } catch (e: NumberFormatException) {
            logE("$e")
            0.0
        } catch (e: NullPointerException) {
            logE("$e")
            0.0
        }
    }

    fun onLocationCurrentChoice() {
        preferences.edit().putInt(WIDGET_LOCATION_TYPE + widgetId, CURRENT.ordinal).apply()
        mutableLocationType.value = CURRENT
        scope.launch {
            restoreCurrentLocation()
        }
    }

    fun onLocationTypeCertainChoice(place: PlaceEntity) {
        preferences.edit()
            .putInt(WIDGET_LOCATION_TYPE + widgetId, CERTAIN.ordinal).apply()
        preferences.edit()
            .putString(WIDGET_LATITUDE + widgetId, place.lat.toString()).apply()
        preferences.edit()
            .putString(WIDGET_LONGITUDE + widgetId, place.lon.toString()).apply()
        preferences.edit()
            .putString(WIDGET_LOCATION_NAME + widgetId, place.title).apply()
        mutableLocationType.value = CERTAIN
        mutableWidgetPlace.value = place
        scope.launch {
            repository.savePlace(place)
            repository.savePlaceToHistory(place)
        }
    }

    fun saveSwitcherState(key: String, checked: Boolean) {
        preferences.edit().putBoolean(key + widgetId, checked).apply()
    }

    fun saveSeekBarState(progress: Int) {
        preferences.edit()
            .putInt(WIDGET_TRANSPARENCY + widgetId, ALPHA_MAX_VALUE - progress)
            .apply()
    }

    class SettingsListViewModelFactory @Inject constructor(
        var repository: DataRepository,
        var context: Context
    ) : ViewModelAssistedFactory<SettingWidgetViewModel> {

        override fun create(arguments: Bundle): SettingWidgetViewModel {
            val id = arguments.getInt(ARGUMENT_WIDGET,AppWidgetManager.INVALID_APPWIDGET_ID)
            return SettingWidgetViewModel(id, repository, context)
        }
    }
}