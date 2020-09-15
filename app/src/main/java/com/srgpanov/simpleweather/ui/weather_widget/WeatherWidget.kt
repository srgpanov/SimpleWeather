package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.other.*


class WeatherWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_CHANGE = "com.srgpanov.simpleweather.ui.weather_widget_ACTION_CHANGE"
        const val ACTION_SHOW_WEATHER =
            "com.srgpanov.simpleweather.ui.weather_widget_ACTION_SHOW_WEATHER"
        const val PLACE_ENTITY_KEY = "PLACE_ENTITY_KEY"
        const val ALPHA_MAX_VALUE = 255
        const val ARG_REFRESH = "com.srgpanov.simpleweather.ui.weather_widget_ARG_REFRESH"

        fun updateWidget(widgetID: Int, refresh: Boolean = false) {
            val factory = App.instance.appComponent.getWidgetHelperFactory()
            val helper = factory.create(widgetID)
            helper.updateWidget(refresh)
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
            val shouldRefresh = intent?.getBooleanExtra(ARG_REFRESH, false) ?: false
            Log.d("WeatherWidget", "onReceive: $shouldRefresh")
            Log.d("WeatherWidget", "onReceive: ${intent?.extras}")
            if (extras != null) {
                widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }
            logD("onReceive widgetId $widgetId")
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                updateWidget(widgetId, shouldRefresh)
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
        for (id in appWidgetIds) {
            updateWidget(id)
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
        logD("onDeleted")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        logD("onDisabled")
    }
}