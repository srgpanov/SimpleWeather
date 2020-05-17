package com.srgpanov.simpleweather.ui.weather_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.App
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WeatherWidget : AppWidgetProvider() {

    companion object {
        private val repository = DataRepositoryImpl()
        const val WIDGET_ID = "WIDGET_ID"
        const val ACTION_CHANGE = "com.srgpanov.simpleweather.ui.weather_widget_ACTION_CHANGE"
        const val ACTION_SHOW_WEATHER =
            "com.srgpanov.simpleweather.ui.weather_widget_ACTION_SHOW_WEATHER"
        const val PLACE_ENTITY_KEY = "PLACE_ENTITY_KEY"
        private val ALPHA_MAX_VALUE = 255

        fun updateWidget(
            context: Context,
            widgetID: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                logD("updateWidget $widgetID")
                var widgetView = RemoteViews(context.packageName, R.layout.widget_layout)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                showLoading(widgetView, appWidgetManager, widgetID)
                val sp = PreferenceManager.getDefaultSharedPreferences(App.instance)
                val icons = sp.getBoolean(WIDGET_ICONS + widgetID, false)
                val isLightTheme = sp.getBoolean(WIDGET_LIGHT_THEME + widgetID, false)
                val showTimeUpdate = sp.getBoolean(WIDGET_SHOW_TIME_UPDATE + widgetID, false)
                val transparency = sp.getInt(WIDGET_TRANSPARENCY + widgetID, ALPHA_MAX_VALUE)
                val locationTitle = sp.getString(WIDGET_LOCATION_NAME + widgetID, "")
                val locationLatitude: Double = getCoordinate(WIDGET_LATITUDE + widgetID, sp)
                val locationLongitude: Double = getCoordinate(WIDGET_LONGITUDE + widgetID, sp)
                val locationType =
                    sp.getInt(WIDGET_LOCATION_TYPE + widgetID, LocationType.CURRENT.ordinal)
                val locationProvider = LocationProvider(LocationType.values()[locationType])
                val shownGeoPoint =
                    if (locationType == LocationType.CURRENT.ordinal) {
                        locationProvider.getGeoPoint()
                    } else {

                        GeoPoint(locationLatitude, locationLongitude)
                    }
                var locationName = if (locationType == LocationType.CURRENT.ordinal) {
                    getLocationName(shownGeoPoint, context)
                } else {
                    locationTitle ?: context.getString(R.string.refresh_required)
                }
                val weatherResponse = if (shownGeoPoint != null) {
                    repository.getFreshWeather(shownGeoPoint)
                } else {
                    showError(widgetView)
                    return@launch
                }

                widgetView = when (weatherResponse) {
                    is ResponseResult.Success -> updateWidgetView(weatherResponse.data, widgetView)
                    is ResponseResult.Failure -> updateWidgetView(
                        widgetView,
                        context,
                        shownGeoPoint
                    )
                }
                val backgroundColor =
                    if (isLightTheme) {
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
                val backgroundTitleColor =
                    if (isLightTheme) {
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
                val updateIntent = Intent(context, WeatherWidget::class.java)
                updateIntent.action = ACTION_CHANGE
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                var pIntent = PendingIntent.getBroadcast(context, widgetID, updateIntent, 0)
                widgetView.setOnClickPendingIntent(R.id.widget_refresh_ib, pIntent)

                val placeEntity = if (locationType == LocationType.CURRENT.ordinal) {
                    PlaceEntity(locationName, shownGeoPoint.lat, shownGeoPoint.lon)
                } else {
                    PlaceEntity(locationName, locationLatitude, locationLongitude)
                }
                placeEntity.favorite=true
                val openActivityIntent = Intent(context, MainActivity::class.java)
                openActivityIntent.action = ACTION_SHOW_WEATHER
                openActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                openActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                openActivityIntent.putExtra(PLACE_ENTITY_KEY,placeEntity)
                pIntent = PendingIntent.getActivity(context, widgetID, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                widgetView.setOnClickPendingIntent(R.id.widget_container, pIntent)

                val textColor = if (isLightTheme) Color.BLACK else Color.WHITE
                val refreshButtonImage =
                    if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp
                val nightTempTextColor =
                    ContextCompat.getColor(context, R.color.widget_black_night_text)
                if (showTimeUpdate) {
                    val timeOfLastUpdate = getTimeOfLastUpdate(repository, shownGeoPoint)
                    val comma = if (timeOfLastUpdate != null) ", " else ""
                    locationName = "${timeOfLastUpdate ?: ""}$comma $locationName"
                }
                if (weatherResponse is ResponseResult.Success) {
                    widgetView.setTextViewText(R.id.widget_place_name_tv, locationName)
                }
                widgetView.setInt(R.id.widget_container, "setBackgroundColor", backgroundColor)
                widgetView.setInt(
                    R.id.widget_place_name_container,
                    "setBackgroundColor",
                    backgroundTitleColor
                )
                widgetView.setTextColor(R.id.widget_place_name_tv, textColor)
                widgetView.setTextColor(R.id.widget_temp_tv, textColor)
                widgetView.setTextColor(R.id.widget_day_temp_tv, textColor)
                widgetView.setTextColor(R.id.widget_night_temp_tv, nightTempTextColor)
                widgetView.setImageViewResource(R.id.widget_refresh_ib, refreshButtonImage)
                hideLoading(widgetView, appWidgetManager, widgetID)
                appWidgetManager.updateAppWidget(widgetID, widgetView)


            }
        }

        private suspend fun updateWidgetView(
            widgetView: RemoteViews,
            context: Context,
            geoPoint: GeoPoint?
        ): RemoteViews {
            widgetView.setTextViewText(
                R.id.widget_place_name_tv,
                context.getString(R.string.refresh_required)
            )
            return if (geoPoint != null) {
                val cachedWeather = repository.getCachedWeather(geoPoint)
                if (cachedWeather != null) {
                    updateWidgetView(cachedWeather, widgetView)
                } else {
                    widgetView
                }
            } else {
                widgetView
            }

        }

        suspend fun getLocationName(
            shownGeoPoint: GeoPoint?,
            context: Context
        ): String {
            return if (shownGeoPoint != null) {
                getPlaceTitle(repository, shownGeoPoint)
            } else {
                context.getString(R.string.refresh_required)
            }
        }

        fun getCoordinate(key: String, sp: SharedPreferences): Double {
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

        fun showLoading(
            widgetView: RemoteViews,
            appWidgetManager: AppWidgetManager,
            widgetID: Int
        ) {
            widgetView.setViewVisibility(R.id.widget_progress_bar, View.VISIBLE)
            widgetView.setViewVisibility(R.id.widget_refresh_ib, View.INVISIBLE)
            appWidgetManager.partiallyUpdateAppWidget(widgetID, widgetView)
        }

        fun hideLoading(
            widgetView: RemoteViews,
            appWidgetManager: AppWidgetManager,
            widgetID: Int
        ) {
            widgetView.setViewVisibility(R.id.widget_progress_bar, View.INVISIBLE)
            widgetView.setViewVisibility(R.id.widget_refresh_ib, View.VISIBLE)
            appWidgetManager.partiallyUpdateAppWidget(widgetID, widgetView)
        }


        suspend fun getTimeOfLastUpdate(
            repository: DataRepositoryImpl,
            geoPoint: GeoPoint?
        ): String? {
            logD("getTimeOfLastUpdate $geoPoint id ${geoPoint?.pointToId()}")
            if (geoPoint != null) {
                val cachedWeather = repository.getOneCallTable(geoPoint)
                if (cachedWeather != null) {
                    val time = cachedWeather.timeStamp
                    logD("getTimeOfLastUpdate returned formatted Date")
                    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
                }
            }
            logD("getTimeOfLastUpdate returned null")
            return null
        }

        private suspend fun getPlaceTitle(
            repository: DataRepositoryImpl,
            shownGeoPoint: GeoPoint
        ): String {
            val placeResponse = repository.getPlaceByGeoPoint(shownGeoPoint)
            return when (placeResponse) {
                is ResponseResult.Success -> placeResponse.data.toEntity().title
                is ResponseResult.Failure -> {
                    logE("widget response error")
                    ""
                }
            }
        }

        private fun showError(widgetView: RemoteViews) {
//todo
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

    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        logD("onEnabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        logD("onReceive $intent")
        if (intent?.action.equals(ACTION_CHANGE, true)) {
            var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            val extras = intent?.extras
            if (extras != null) {
                widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }
            logD("onReceive widgetId $widgetId")
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                if (context != null) {
                    updateWidget(context, widgetId)
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        logD("onUpdate $appWidgetIds")
        appWidgetIds.forEach { id ->
            updateWidget(context, id)
        }
    }


    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { id ->
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().remove(WIDGET_ICONS + id).apply()
            sharedPreferences.edit().remove(WIDGET_LIGHT_THEME + id).apply()
            sharedPreferences.edit().remove(WIDGET_SHOW_TIME_UPDATE + id).apply()
            sharedPreferences.edit().remove(WIDGET_TRANSPARENCY + id).apply()
            sharedPreferences.edit().remove(WIDGET_LOCATION_TYPE + id).apply()
            sharedPreferences.edit().remove(WIDGET_LATITUDE + id).apply()
            sharedPreferences.edit().remove(WIDGET_LONGITUDE + id).apply()
        }

        super.onDeleted(context, appWidgetIds)
        logD("onDeleted")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        logD("onDisabled")
    }
}