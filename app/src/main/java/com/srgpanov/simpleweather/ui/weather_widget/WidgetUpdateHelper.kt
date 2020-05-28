package com.srgpanov.simpleweather.ui.weather_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.values
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WidgetUpdateHelper @Inject constructor(
    private val context: Context,
    private val repository: DataRepository,
    private val sp: SharedPreferences
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var widgetView: RemoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
    private var appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    private var widgetID: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private var icons: Boolean = false
    private var isLightTheme: Boolean = false
    private var showTimeUpdate: Boolean = false
    private var transparency: Int = WeatherWidget.ALPHA_MAX_VALUE
    private var locationTitle: String? = null
    private var locationLatitude: Double = 0.0
    private var locationLongitude: Double = 0.0
    private var locationType = CURRENT.ordinal
    private var locationProvider = LocationProvider(values()[locationType])
    private lateinit var observePlace: PlaceEntity

    fun updateWidget(widgetID: Int) = scope.launch {
        setupId(widgetID)
        showLoading(this@WidgetUpdateHelper.widgetID)
        val place = getObservablePlace()
        if (place != null) {
            observePlace = place
        } else {
            hideLoading(this@WidgetUpdateHelper.widgetID)
            showError()
            return@launch
        }
        var locationName = observePlace.title
        val response = observePlace.oneCallResponse
        val oneCallTable = repository.getOneCallTable(observePlace.toGeoPoint())
        val isFresh = oneCallTable?.isFresh() ?: false
        if (response != null && isFresh) {
            updateWidgetView(response, widgetView)
        } else {
            val weatherResponse = repository.getFreshWeather(observePlace.toGeoPoint())
            widgetView = when (weatherResponse) {
                is ResponseResult.Success -> updateWidgetView(weatherResponse.data, widgetView)
                is ResponseResult.Failure -> updateWidgetView(widgetView, observePlace.toGeoPoint())
            }
        }
        setupBackground()
        setupClickListeners()
        val textColor = if (isLightTheme) Color.BLACK else Color.WHITE
        val refreshButtonImage =
            if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp
        val nightTempTextColor =
            ContextCompat.getColor(context, R.color.widget_black_night_text)
        if (showTimeUpdate) {
            val timeOfLastUpdate = getTimeOfLastUpdate(observePlace.toGeoPoint())
            val comma = if (timeOfLastUpdate != null) ", " else ""
            locationName = "${timeOfLastUpdate ?: ""}$comma $locationName"
        }
        widgetView.setTextViewText(R.id.widget_place_name_tv, locationName)
        widgetView.setTextColor(R.id.widget_place_name_tv, textColor)
        widgetView.setTextColor(R.id.widget_temp_tv, textColor)
        widgetView.setTextColor(R.id.widget_day_temp_tv, textColor)
        widgetView.setTextColor(R.id.widget_night_temp_tv, nightTempTextColor)
        widgetView.setImageViewResource(R.id.widget_refresh_ib, refreshButtonImage)
        hideLoading(this@WidgetUpdateHelper.widgetID)
        appWidgetManager.updateAppWidget(this@WidgetUpdateHelper.widgetID, widgetView)
    }

    private fun setupBackground() {
        val backgroundColor = if (isLightTheme) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.widget_white),
                transparency
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.widget_black),
                transparency.also { logD("alpha $it") }
            )
        }
        val backgroundTitleColor = if (isLightTheme) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.widget_white_title),
                transparency
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.widget_black_title),
                transparency
            )
        }
        widgetView.setInt(R.id.widget_container, "setBackgroundColor", backgroundColor)
        widgetView.setInt(
            R.id.widget_place_name_container,
            "setBackgroundColor",
            backgroundTitleColor
        )

    }

    private fun setupId(widgetID: Int) {
        this.widgetID = widgetID
        icons = sp.getBoolean(WIDGET_ICONS + widgetID, false)
        isLightTheme = sp.getBoolean(WIDGET_LIGHT_THEME + widgetID, false)
        showTimeUpdate = sp.getBoolean(WIDGET_SHOW_TIME_UPDATE + widgetID, false)
        transparency = sp.getInt(WIDGET_TRANSPARENCY + widgetID, WeatherWidget.ALPHA_MAX_VALUE)
        locationTitle = sp.getString(WIDGET_LOCATION_NAME + widgetID, "")
        locationLatitude = getCoordinate(WIDGET_LATITUDE + widgetID)
        locationLongitude = getCoordinate(WIDGET_LONGITUDE + widgetID)
        locationType = sp.getInt(WIDGET_LOCATION_TYPE + widgetID, CURRENT.ordinal)
    }

    private fun setupClickListeners() {
        val updateIntent = Intent(context, WeatherWidget::class.java)
        updateIntent.action = WeatherWidget.ACTION_CHANGE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        var pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0)
        widgetView.setOnClickPendingIntent(R.id.widget_refresh_ib, pIntent)

        if (this::observePlace.isInitialized) {
            val openActivityIntent = Intent(context, MainActivity::class.java)
            openActivityIntent.action = WeatherWidget.ACTION_SHOW_WEATHER
            openActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            openActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            openActivityIntent.putExtra(WeatherWidget.PLACE_ENTITY_KEY, observePlace)
            pIntent = PendingIntent.getActivity(
                context,
                widgetID,
                openActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            widgetView.setOnClickPendingIntent(R.id.widget_container, pIntent)
        }
    }


    suspend fun getObservablePlace(): PlaceEntity? {
        return if (locationType == CURRENT.ordinal) {
            val geoPoint = locationProvider.getGeoPoint()
            if (geoPoint != null) {
                repository.getPlaceByGeoPoint(geoPoint)
            } else {
                null
            }
        } else {
            GeoPoint(locationLatitude, locationLongitude)
            PlaceEntity(
                locationTitle ?: context.getString(R.string.refresh_required),
                locationLatitude,
                locationLongitude
            )
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

    suspend fun getLocationName(): String {
        return getObservablePlace()?.title ?: context.getString(R.string.refresh_required)
    }


    private fun updateWidgetView(
        oneCallResponse: OneCallResponse,
        widgetView: RemoteViews
    ): RemoteViews {
        val tempCurrent = oneCallResponse.current.tempFormatted()
        val tempDay = oneCallResponse.daily[0].temp.dayFormated()
        val tempNight = oneCallResponse.daily[0].temp.nightFormated()
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
            val cachedWeather = repository.getCachedWeather(geoPoint)
            if (cachedWeather != null) {
                updateWidgetView(cachedWeather, widgetView)
            } else {
                widgetView
            }
        }

    }

    suspend fun getTimeOfLastUpdate(geoPoint: GeoPoint): String? {
        logD("getTimeOfLastUpdate $geoPoint id ${geoPoint.pointToId()}")
        val cachedWeather = repository.getOneCallTable(geoPoint)
        if (cachedWeather != null) {
            val time = cachedWeather.timeStamp
            logD("getTimeOfLastUpdate returned formatted Date")
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
        }
        logD("getTimeOfLastUpdate returned null")
        return null
    }

    private fun getCoordinate(key: String): Double {
        return try {
            sp.getString(key, "")?.toDouble()!!
        } catch (e: NumberFormatException) {
            logE("$e")
            0.0
        } catch (e: NullPointerException) {
            logE("$e")
            0.0
        }
    }


}
