package com.srgpanov.simpleweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity

@Database(entities = arrayOf(
    PlaceEntity::class,
    WeatherEntity::class),version = 1)
@TypeConverters(RoomConverter::class)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDao

    companion object {
        private lateinit var INSTANCE: WeatherDataBase

        fun getInstance(context: Context): WeatherDataBase {
            if (!Companion::INSTANCE.isInitialized) {
                synchronized(WeatherDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                        WeatherDataBase::class.java, "weatherDb")
                        .build()
                }
            }
            return INSTANCE
        }


    }
}