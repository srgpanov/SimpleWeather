package com.srgpanov.simpleweather.ui

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.facebook.stetho.Stetho
import com.srgpanov.simpleweather.other.logD

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this);


    }


}