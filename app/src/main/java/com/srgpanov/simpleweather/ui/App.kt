package com.srgpanov.simpleweather.ui

import android.app.Application
import com.facebook.stetho.Stetho

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