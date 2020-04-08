package com.srgpanov.simpleweather.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.WeatherEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint

@Dao
interface WeatherDao {

    @Query("SELECT * from placeentity WHERE isFavorite==1")
    suspend fun getFavoritesPlaces(): List<PlaceEntity>

    @Query("SELECT * from placeentity WHERE isCurrent==1")
    suspend fun getCurrentLocation(): PlaceEntity?

    @Insert(onConflict = REPLACE)
    suspend fun savePlace(placesEntity: PlaceEntity)

    @Insert(onConflict = REPLACE)
    suspend fun saveResponse(weatherEntity: WeatherEntity)
    @Delete
    suspend fun removePlace(placesEntity: PlaceEntity)

    @Query("SELECT * from weatherentity")
    suspend fun getLastResponse(): List<WeatherEntity>
    @Query("SELECT * from  weatherentity WHERE id=:geoPoint")
    suspend fun getResponse(geoPoint: String):WeatherEntity?

    @Query("SELECT * from  placeentity WHERE lat=:lat AND  lon=:lon")
    suspend fun getPlace(lat: Double, lon: Double): PlaceEntity?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun changeFavoriteStatus(placeEntity: PlaceEntity)

}