package com.srgpanov.simpleweather

import android.app.Application
import com.facebook.stetho.Stetho
import com.srgpanov.simpleweather.di.components.AppComponent
import com.srgpanov.simpleweather.di.components.DaggerAppComponent
import com.srgpanov.simpleweather.di.module.AppModule

class App : Application() {
    lateinit var appComponent:AppComponent
        private set

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this)).build()
        Stetho.initializeWithDefaults(this)
    }
}