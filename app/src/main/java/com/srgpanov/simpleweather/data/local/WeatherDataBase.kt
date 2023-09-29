package com.srgpanov.simpleweather.data.local

import androidx.room.Database
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

}