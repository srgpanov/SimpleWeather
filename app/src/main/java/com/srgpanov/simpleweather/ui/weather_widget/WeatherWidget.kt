package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.other.*


class WeatherWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_CHANGE = "com.srgpanov.simpleweather.ui.weather_widget_ACTION_CHANGE"
        const val ACTION_SHOW_WEATHER =
            "com.srgpanov.simpleweather.ui.weather_widget_ACTION_SHOW_WEATHER"
        const val PLACE_ENTITY_KEY = "PLACE_ENTITY_KEY"
        val ALPHA_MAX_VALUE = 255

        fun updateWidget(
            widgetID: Int
        ) {
            val helper = App.instance.appComponent.getWidgetHelper()
            helper.updateWidget(widgetID)
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
                updateWidget(widgetId)
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
            updateWidget( id)
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