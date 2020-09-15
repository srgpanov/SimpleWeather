package com.srgpanov.simpleweather.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Singleton
class PreferencesStorage @Inject constructor(val context: Context) {
    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    companion object {
        const val TEMP_MEASUREMENT = "TEMP_MEASUREMENT"
        const val WIND_MEASUREMENT = "WIND_MEASUREMENT"
        const val PRESSURE_MEASUREMENT = "PRESSURE_MEASUREMENT"


        const val LOCATION_TYPE = "LOCATION_TYPE"
        const val LAST_KNOWN_LOCATION = "LAST_KNOWN_LOCATION"

        const val IS_FIRST_START = "IS_FIRST_START"
    }

    fun provideSharedPref(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(preferences: String, default: T): ReadWriteProperty<Any?, T> {
        return when (default) {
            is String -> PreferencesDelegate(this.preferences, preferences, default)
            is Int -> PreferencesDelegate(this.preferences, preferences, default)
            is Boolean -> PreferencesDelegate(this.preferences, preferences, default)
            is Float -> PreferencesDelegate(this.preferences, preferences, default)
            is Long -> PreferencesDelegate(this.preferences, preferences, default)
            else -> throw UnsupportedOperationException("Type can't saved in preferences")
        }
    }

    @Suppress("UNCHECKED_CAST")
    class PreferencesDelegate<TValue>(
        private val preferences: SharedPreferences,
        private val name: String,
        private val defValue: TValue
    ) : ReadWriteProperty<Any?, TValue> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): TValue {
            with(preferences) {
                return when (defValue) {
                    is Boolean -> (getBoolean(name, defValue) as? TValue) ?: defValue
                    is Int -> (getInt(name, defValue) as TValue) ?: defValue
                    is Float -> (getFloat(name, defValue) as TValue) ?: defValue
                    is Long -> (getLong(name, defValue) as TValue) ?: defValue
                    is String -> (getString(name, defValue) as TValue) ?: defValue
                    else -> throw NotFoundRealizationException(defValue)
                }
            }
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: TValue) {
            with(preferences.edit()) {
                when (value) {
                    is Boolean -> putBoolean(name, value)
                    is Int -> putInt(name, value)
                    is Float -> putFloat(name, value)
                    is Long -> putLong(name, value)
                    is String -> putString(name, value)
                    else -> throw NotFoundRealizationException(value)
                }
                apply()
            }
        }

        class NotFoundRealizationException(defValue: Any?) :
            Exception("not found realization for $defValue")
    }
}


