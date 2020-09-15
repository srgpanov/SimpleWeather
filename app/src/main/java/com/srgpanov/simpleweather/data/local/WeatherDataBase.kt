package com.srgpanov.simpleweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.srgpanov.simpleweather.data.models.entity.*

@Database(
    entities = [
        PlaceEntity::class,
        FavoriteEntity::class,
        SearchHistoryEntity::class,
        CurrentEntity::class,
        OneCallEntity::class,
        SimpleWeatherEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(RoomConverter::class)
abstract class WeatherDataBase : RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDao


    companion object {
        private lateinit var INSTANCE: WeatherDataBase

        fun getInstance(context: Context): WeatherDataBase {
            if (!Companion::INSTANCE.isInitialized) {
                synchronized(WeatherDataBase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        WeatherDataBase::class.java, "weatherDb"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }


    }
}