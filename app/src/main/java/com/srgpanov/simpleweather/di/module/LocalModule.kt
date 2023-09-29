package com.srgpanov.simpleweather.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.local.WeatherDao
import com.srgpanov.simpleweather.data.local.WeatherDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Singleton
    @Provides
    fun getDataBase(context: Context): WeatherDataBase {
        return Room.databaseBuilder(
            context = context,
            klass = WeatherDataBase::class.java,
            name = "weatherDb"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun getWeatherDao(dataBase: WeatherDataBase): WeatherDao {
        return dataBase.weatherDataDao()
    }

    @Singleton
    @Provides
    fun getLocalDataSource(dao: WeatherDao): LocalDataSourceImpl {
        return LocalDataSourceImpl(dao)
    }

    @Singleton
    @Provides
    fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}