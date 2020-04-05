package com.srgpanov.simpleweather.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.srgpanov.simpleweather.data.entity.PlaceEntity

@Database(entities = arrayOf(PlaceEntity::class),version = 1)
abstract class WeatherDataBase: RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDao

    companion object {
        private lateinit var INSTANCE: WeatherDataBase

        fun getInstance(context: Context): WeatherDataBase {
            if (!::INSTANCE.isInitialized) {
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