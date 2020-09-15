package com.srgpanov.simpleweather.ui.weather_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.location.LocationProvider
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget.Companion.ARG_REFRESH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WidgetUpdateHelper @Inject constructor(
    private val widgetID: Int,
    private val context: Context,
    private val repository: DataRepository,
    preferences: PreferencesStorage,
    private val locationProvider: LocationProvider
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()
    private var widgetView: RemoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
    private var appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)

    private var isLightTheme: Boolean by preferences(WIDGET_LIGHT_THEME + widgetID, false)
    private var showTimeUpdate: Boolean by preferences(WIDGET_SHOW_TIME_UPDATE + widgetID, false)
    private var transparency: Int
            by preferences(WIDGET_TRANSPARENCY + widgetID, WeatherWidget.ALPHA_MAX_VALUE)

    private var locationTitle: String by preferences(WIDGET_LOCATION_NAME + widgetID, "")

    private var _locationLatitude: String by preferences(WIDGET_LATITUDE + widgetID, "")
    private var locationLatitude: Double
        get() = _locationLatitude.toDoubleOrNull() ?: 0.0
        set(value) {
            _locationLatitude = value.toString()
        }

    private var _locationLongitude: String by preferences(WIDGET_LONGITUDE + widgetID, "")
    private var locationLongitude: Double
        get() = _locationLongitude.toDoubleOrNull() ?: 0.0
        set(value) {
            _locationLongitude = value.toString()
        }


    private var locationType: Int by preferences(WIDGET_LOCATION_TYPE + widgetID, CURRENT.ordinal)
    private lateinit var observePlaceView: PlaceViewItem

    fun updateWidget(shouldRefresh: Boolean = false) = launch {
        Log.d("WidgetUpdateHelper", "updateWidget: $widgetID")
        showLoading(widgetID)
        val place = getObservablePlace()
        if (place != null) {
            observePlaceView = place
        } else {
            hideLoading(widgetID)
            showError()
            return@launch
        }
        var locationName = observePlaceView.title
        val response = observePlaceView.oneCallResponse
        val oneCallTable = repository.getOneCallTable(observePlaceView.toGeoPoint())
        val isFresh = oneCallTable?.oneCallResponse?.isFresh ?: false
        val useCachedWeather = response != null && isFresh && !shouldRefresh
        if (useCachedWeather) {
            updateWidgetView(response!!, widgetView)
        } else {
            val weatherResponse = repository.getFreshWeather(observePlaceView.toGeoPoint())
            widgetView = if (weatherResponse != null) {
                updateWidgetView(weatherResponse, widgetView)
            } else {
                updateWidgetView(widgetView, observePlaceView.toGeoPoint())
            }
        }
        setupBackground()
        setupClickListeners()
        val textColor = if (isLightTheme) Color.BLACK else Color.WHITE
        val refreshButtonImage =
            if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp
        val nightTempTextColor = context.getColorCompat(R.color.widget_black_night_text)
        if (showTimeUpdate) {
            val timeOfLastUpdate = getTimeOfLastUpdate(observePlaceView.toGeoPoint())
            val comma = if (timeOfLastUpdate != null) ", " else ""
            locationName = "${timeOfLastUpdate ?: ""}$comma $locationName"
        }
        widgetView.setTextViewText(R.id.widget_place_name_tv, locationName)
        widgetView.setTextColor(R.id.widget_place_name_tv, textColor)
        widgetView.setTextColor(R.id.widget_temp_tv, textColor)
        widgetView.setTextColor(R.id.widget_day_temp_tv, textColor)
        widgetView.setTextColor(R.id.widget_night_temp_tv, nightTempTextColor)
        widgetView.setImageViewResource(R.id.widget_refresh_ib, refreshButtonImage)
        hideLoading(widgetID)
        appWidgetManager.updateAppWidget(widgetID, widgetView)
    }

    private fun setupBackground() {
        val backColor = if (isLightTheme) R.color.widget_white else R.color.widget_black
        val backgroundColor = context.getColorWithAlpha(backColor, transparency)

        val titleColor =
            if (isLightTheme) R.color.widget_white_title else R.color.widget_black_title
        val backgroundTitleColor = context.getColorWithAlpha(titleColor, transparency)

        widgetView.setInt(R.id.widget_container, "setBackgroundColor", backgroundColor)
        widgetView.setInt(
            R.id.widget_place_name_container,
            "setBackgroundColor",
            backgroundTitleColor
        )
    }

    @ColorInt
    private fun Context.getColorWithAlpha(@ColorRes colorId: Int, alpha: Int): Int {
        return ColorUtils.setAlphaComponent(getColorCompat(colorId), alpha)
    }


    private fun setupClickListeners() {
        val updateIntent = Intent(context, WeatherWidget::class.java)
        updateIntent.action = WeatherWidget.ACTION_CHANGE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        updateIntent.putExtra(ARG_REFRESH, true)
        var pIntent = PendingIntent.getBroadcast(
            context,
            widgetID,
            updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        widgetView.setOnClickPendingIntent(R.id.widget_refresh_ib, pIntent)

        if (this::observePlaceView.isInitialized) {
            val openActivityIntent = Intent(context, MainActivity::class.java).apply {
                action = WeatherWidget.ACTION_SHOW_WEATHER
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(WeatherWidget.PLACE_ENTITY_KEY, observePlaceView)
            }
            pIntent = PendingIntent.getActivity(
                context,
                widgetID,
                openActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            widgetView.setOnClickPendingIntent(R.id.widget_container, pIntent)
        }
    }


    private suspend fun getObservablePlace(): PlaceViewItem? {
        return if (locationType == CURRENT.ordinal) {
            val geoPoint = locationProvider.getWeatherGeoPoint()
            if (geoPoint != null) {
                repository.getPlaceByGeoPoint(geoPoint)
            } else {
                null
            }
        } else {
            PlaceViewItem(locationTitle, locationLatitude, locationLongitude)
        }
    }

    private fun hideLoading(
        widgetID: Int
    ) {
        widgetView.setViewVisibility(R.id.widget_progress_bar, View.INVISIBLE)
        widgetView.setViewVisibility(R.id.widget_refresh_ib, View.VISIBLE)
        appWidgetManager.partiallyUpdateAppWidget(widgetID, widgetView)
    }

    private fun showLoading(
        widgetID: Int
    ) {
        widgetView.setViewVisibility(R.id.widget_progress_bar, View.VISIBLE)
        widgetView.setViewVisibility(R.id.widget_refresh_ib, View.INVISIBLE)
        appWidgetManager.partiallyUpdateAppWidget(widgetID, widgetView)
    }

    private fun showError() {
        setupClickListeners()
        setupBackground()
        widgetView.setTextViewText(
            R.id.widget_place_name_tv,
            context.getString(R.string.refresh_required)
        )
        val textColor = if (isLightTheme) Color.BLACK else Color.WHITE
        widgetView.setTextColor(R.id.widget_place_name_tv, textColor)
        val refreshButtonImage =
            if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp
        widgetView.setImageViewResource(R.id.widget_refresh_ib, refreshButtonImage)

        appWidgetManager.updateAppWidget(widgetID, widgetView)
    }


    private fun updateWidgetView(
        oneCallResponse: OneCallResponse,
        widgetView: RemoteViews
    ): RemoteViews {
        val tempCurrent = oneCallResponse.current.tempFormatted()
        val tempDay = oneCallResponse.daily[0].temp.dayFormatted()
        val tempNight = oneCallResponse.daily[0].temp.nightFormatted()
        val weatherIcon = oneCallResponse.current.weather[0].getWeatherIcon()
        widgetView.setTextViewText(R.id.widget_temp_tv, tempCurrent)
        widgetView.setTextViewText(R.id.widget_day_temp_tv, tempDay)
        widgetView.setTextViewText(R.id.widget_night_temp_tv, tempNight)
        widgetView.setImageViewResource(R.id.widget_weather_icon_iv, weatherIcon)
        return widgetView
    }

    private suspend fun updateWidgetView(widgetView: RemoteViews, geoPoint: GeoPoint): RemoteViews {
        widgetView.setTextViewText(
            R.id.widget_place_name_tv,
            context.getString(R.string.refresh_required)
        )
        return run {
            val cachedWeather = repository.getPlaceByGeoPoint(geoPoint)?.oneCallResponse
            if (cachedWeather != null) {
                updateWidgetView(cachedWeather, widgetView)
            } else {
                widgetView
            }
        }

    }

    private suspend fun getTimeOfLastUpdate(geoPoint: GeoPoint): String? {
        val cachedWeather = repository.getOneCallTable(geoPoint)
        if (cachedWeather != null) {
            val time = cachedWeather.oneCallResponse.timeStamp
            return Date(time).format("HH:mm")
        }
        logD("getTimeOfLastUpdate returned null")
        return null
    }


    class Factory @Inject constructor(
        private val context: Context,
        private val repository: DataRepository,
        private val preferences: PreferencesStorage,
        private val locationProvider: LocationProvider
    ) {
        fun create(
            widgetID: Int
        ): WidgetUpdateHelper {
            return WidgetUpdateHelper(
                widgetID,
                context,
                repository,
                preferences,
                locationProvider
            )
        }
    }


}


