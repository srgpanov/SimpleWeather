package com.srgpanov.simpleweather.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.other.logD

@Dao
abstract class WeatherDao {

    @Query("SELECT cityTitle,lat,lon,cityFullName  from FavoriteTable")
    abstract suspend fun getFavoritesPlaces(): List<PlaceEntity>

    @Query("SELECT cityTitle,lat,lon,cityFullName from CurrentTable")
    abstract suspend fun getCurrentLocation(): List<PlaceEntity>

    @Query("SELECT cityTitle,lat,lon,cityFullName  from SearchHistoryTable ORDER BY time DESC ")
    abstract suspend fun getSearchHistory(): List<PlaceEntity>

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveCurrentPlace(placesEntity: CurrentTable)

    @Delete
    abstract suspend fun deleteCurrentPlace(placesEntity: CurrentTable)

    @Transaction
    open suspend fun saveCurrentPlaceWithReplace(placesEntity: CurrentTable) {
        getCurrentLocation().forEach {
            deleteCurrentPlace(it.toCurrentTable())
        }
        saveCurrentPlace(placesEntity)
    }

    @Insert(onConflict = REPLACE)
    abstract suspend fun savePlaceToHistory(place: SearchHistoryTable)

    @Transaction
    open suspend fun savePlaceToHistoryMaxPlace(place: SearchHistoryTable, maxPlace: Int = 30) {
        val placeList = getSearchHistory()
        if (placeList.size >= maxPlace) {
            placeList.forEachIndexed { index, placeEntity ->
                if (index >= maxPlace) {
                    logD("place deleted ${placeEntity.cityFullName}")
                    deletePlaceFromHistory(placeEntity.toSearchHistoryTable())
                }
            }
        }
        savePlaceToHistory(place)
    }

    @Delete
    abstract suspend fun deletePlaceFromHistory(place: SearchHistoryTable)

    @Query("SELECT * from FavoriteTable WHERE id=:pointToId")
    abstract suspend fun placeIsFavorite(pointToId: String): FavoriteTable?

    @Query("DELETE from FavoriteTable WHERE id=:id ")
    abstract suspend fun removeFavoritePlace(id: String): Int

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveFavoritePlace(placeEntity: FavoriteTable)

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveOneCallResponse(weatherEntity: OneCallTable)

    @Query("SELECT * from OneCallTable")
    abstract suspend fun getLastResponse(): List<OneCallTable>

    @Query("SELECT * from  onecalltable WHERE id=:geoPoint")
    abstract suspend fun getOneCallResponse(geoPoint: String): OneCallTable?
    @Query("SELECT * from  SimpleWeatherTable WHERE id=:geoPoint")
    abstract suspend fun getCurrentResponse(geoPoint: String): SimpleWeatherTable?
    @Insert(onConflict = REPLACE)
    abstract suspend fun saveCurrentResponse(response: SimpleWeatherTable)

}