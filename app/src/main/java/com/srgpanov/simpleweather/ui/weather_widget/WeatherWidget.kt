package com.srgpanov.simpleweather.ui.weather_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteConstraintException
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
        const val ACTION_CHANGE = "com.srgpanov.simpleweather.ui.weather_widget_ACTION_CHANGE"
        const val ACTION_SHOW_WEATHER =
            "com.srgpanov.simpleweather.ui.weather_widget_ACTION_SHOW_WEATHER"
        const val PLACE_ENTITY_KEY = "PLACE_ENTITY_KEY"
        val ALPHA_MAX_VALUE = 255

        fun updateWidget(
            context: Context,
            widgetID: Int
        ) {
            val updateHelper=WidgetUpdateHelper(widgetID,context)
            updateHelper.updateWidget()
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