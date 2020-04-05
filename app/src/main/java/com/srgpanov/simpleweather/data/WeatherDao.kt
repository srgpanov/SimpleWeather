package com.srgpanov.simpleweather.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.srgpanov.simpleweather.data.entity.PlaceEntity

@Dao
interface WeatherDao {

    @Query("SELECT * from placeentity WHERE isCurrent==0")
    suspend fun getFavoritesPlaces(): List<PlaceEntity>

    @Query("SELECT * from placeentity WHERE isCurrent==1")
    suspend fun getCurrentLocation(): List<PlaceEntity>

    @Insert(onConflict = REPLACE)
    suspend fun savePlace(placesEntity: PlaceEntity)

//    suspend fun saveResponse(weatherEntity: WeatherEntity)
//    @Delete
//    suspend fun removePlace(placesEntity: PlacesEntity)
    //    @Query("SELECT * from weatherentity ")
//    suspend fun getLastResponse(): List<WeatherEntity>
//
//    @Insert(onConflict = REPLACE)
}