package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.location.LocationProvider
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.di.ViewModelAssistedFactory
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CERTAIN
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingWidgetViewModel(
    widgetId: Int,
    private val repository: DataRepository,
    preferences: PreferencesStorage,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private var _locationLatitude: String by preferences(WIDGET_LATITUDE + widgetId, "")
    private var locationLatitude: Double
        get() = _locationLatitude.toDoubleOrNull() ?: 0.0
        set(value) {
            _locationLatitude = value.toString()
        }

    private var _locationLongitude: String by preferences(WIDGET_LONGITUDE + widgetId, "")
    private var locationLongitude: Double
        get() = _locationLongitude.toDoubleOrNull() ?: 0.0
        set(value) {
            _locationLongitude = value.toString()
        }

    private var locationName: String by preferences(WIDGET_LOCATION_NAME + widgetId, "")

    private var lightTheme: Boolean by preferences(WIDGET_LIGHT_THEME + widgetId, false)
    private var timeOfUpdate: Boolean by preferences(WIDGET_SHOW_TIME_UPDATE + widgetId, false)
    private var transparencyPref: Int
            by preferences(WIDGET_TRANSPARENCY + widgetId, ALPHA_MAX_VALUE)

    private var showTimeUpdate: Boolean by preferences(WIDGET_SHOW_TIME_UPDATE + widgetId, false)
    private var _locationTypePref: Int
            by preferences(WIDGET_LOCATION_TYPE + widgetId, CURRENT.ordinal)

    private var locationTypePref: LocationType
        get() = (if (_locationTypePref == CURRENT.ordinal) CURRENT else CERTAIN)
        set(value) {
            _locationTypePref = value.ordinal
        }


    private val mutableLocationDescription = MutableLiveData<String>()
    private val mutableWidgetPlace = MutableLiveData<PlaceViewItem>()
    val isLightTheme = MutableLiveDataKt(lightTheme)
    val timeOfLastUpdate = MutableLiveDataKt(timeOfUpdate)
    val transparency = MutableLiveDataKt(transparencyPref)
    val locationType = MutableLiveDataKt(locationTypePref)
    val widgetPlaceView: LiveData<PlaceViewItem> = mutableWidgetPlace

    companion object {
        const val ALPHA_MAX_VALUE = 255
        const val ARGUMENT_WIDGET = "ARGUMENT_WIDGET"
    }

    init {
        restoreSettings()
        logD("widget Id $widgetId")
    }

    private fun restoreSettings() {
        scope.launch {
            when (locationTypePref) {
                CURRENT -> restoreCurrentLocation()
                CERTAIN -> restoreCertainLocation()
            }
        }

    }

    private suspend fun restoreCurrentLocation() {
        val geoPoint = locationProvider.getWeatherGeoPoint()
        val place =
            repository.getPlaceByGeoPoint(geoPoint ?: GeoPoint())
        place?.let { mutableWidgetPlace.postValue(it) }
            ?: logE("restoreCurrentLocation place==null")

    }

    private suspend fun restoreCertainLocation() {
        val place = repository.getPlaceByGeoPoint(GeoPoint(locationLatitude, locationLongitude))
        place?.let {
            mutableWidgetPlace.postValue(it)
            mutableLocationDescription.postValue(it.title)
        }
    }


    fun onLocationCurrentChoice() {
        locationTypePref = CURRENT
        locationType.value = CURRENT
        scope.launch {
            restoreCurrentLocation()
        }
    }

    fun onLocationTypeCertainChoice(placeView: PlaceViewItem) {
        locationTypePref = CERTAIN
        locationLatitude = placeView.lat
        locationLongitude = placeView.lon
        locationName = placeView.title
        locationType.value = CERTAIN
        mutableWidgetPlace.value = placeView
        scope.launch {
            repository.savePlace(placeView)
            repository.savePlaceToHistory(placeView)
        }
    }

    fun saveSwitcherState(key: String, checked: Boolean) {
        return when (key) {
            WIDGET_SHOW_TIME_UPDATE -> showTimeUpdate = checked
            WIDGET_LIGHT_THEME -> lightTheme = checked
            else -> throw  IllegalStateException("preferences not implemented")
        }
    }

    fun saveSeekBarState(progress: Int) {
        transparencyPref = ALPHA_MAX_VALUE - progress
    }

    class SettingsListViewModelFactory @Inject constructor(
        private val repository: DataRepository,
        private val preferences: PreferencesStorage,
        private val locationProvider: LocationProvider
    ) : ViewModelAssistedFactory<SettingWidgetViewModel> {

        override fun create(arguments: Bundle): SettingWidgetViewModel {
            val id = arguments.getInt(ARGUMENT_WIDGET, AppWidgetManager.INVALID_APPWIDGET_ID)
            return SettingWidgetViewModel(id, repository, preferences, locationProvider)
        }
    }
}