package com.srgpanov.simpleweather.data.local

import android.util.Log
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.entity.query_entity.PlacesWithSimpleWeather
import com.srgpanov.simpleweather.data.models.entity.query_entity.PlacesWithWeather
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WeatherDao {

    @Transaction
    @Query("SELECT *  from place, favorite WHERE id==idFavorite")
    abstract fun getFavorites(): Flow<List<PlacesWithSimpleWeather>>

    @Transaction
    @Query("SELECT * from place,current WHERE id==idCurrent")
    abstract fun getCurrentLocationFlow(): Flow<PlacesWithWeather?>


    @Transaction
    @Query("SELECT * from current ")
    abstract suspend fun getCurrentLocation(): List<CurrentEntity>

    @Transaction
    @Query("SELECT * from place,search_history WHERE id==idSearch ORDER BY timeStamp DESC ")
    abstract suspend fun getSearchHistory(): List<PlacesWithWeather>

    @Transaction
    @Query("SELECT * from place WHERE id==:id")
    abstract suspend fun getPlaceById(id: String): PlacesWithWeather?

    @Transaction
    @Query("SELECT * from place WHERE id==:id")
    abstract fun getPlaceByIdFlow(id: String): Flow<PlacesWithWeather>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertPlace(placeEntity: PlaceEntity): Long

    @Update
    abstract suspend fun update(placeEntity: PlaceEntity)

    @Query("SELECT * from place WHERE id=:point")
    abstract suspend fun placeIsInDb(point: String): PlaceEntity?

    @Transaction
    open suspend fun insertOrUpdatePlace(placeEntity: PlaceEntity) {
        val id = insertPlace(placeEntity)
        if (id == -1L) update(placeEntity)
    }

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveCurrentPlace(placesEntity: CurrentEntity): Long

    @Delete
    abstract suspend fun deleteCurrentPlace(placesEntity: CurrentEntity)

    @Transaction
    open suspend fun saveCurrentPlaceWithReplace(placesEntity: CurrentEntity) {
        val listOfCurrent = getCurrentLocation()
        Log.d("WeatherDao", "saveCurrentPlaceWithReplace: $listOfCurrent")
        for (item in listOfCurrent) {
            deleteCurrentPlace(item)
        }
        val saveCurrentPlace = saveCurrentPlace(placesEntity)
        Log.d("WeatherDao", "saveCurrentPlaceWithReplace: $saveCurrentPlace")
    }

    @Insert(onConflict = REPLACE)
    abstract suspend fun savePlaceToHistory(place: SearchHistoryEntity)

    @Delete
    abstract suspend fun deletePlaceFromHistory(place: SearchHistoryEntity)

    @Query("SELECT * from favorite WHERE idFavorite=:pointToId")
    abstract suspend fun placeIsFavorite(pointToId: String): FavoriteEntity?

    @Query("SELECT * from favorite WHERE idFavorite==:id")
    abstract fun placeIsFavoriteFlow(id: String): Flow<FavoriteEntity?>

    @Query("DELETE from favorite WHERE idFavorite=:id ")
    abstract suspend fun removeFavoritePlace(id: String): Int

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveFavoritePlace(place: FavoriteEntity)

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveOneCallResponse(weather: OneCallEntity)

    @Query("SELECT * from one_call")
    abstract suspend fun getLastResponse(): List<OneCallEntity>

    @Query("SELECT * from  one_call WHERE id=:geoPoint")
    abstract suspend fun getOneCallResponse(geoPoint: String): OneCallEntity?

    @Query("SELECT * from  one_call WHERE id=:geoPoint")
    abstract fun getOneCallResponseFlow(geoPoint: String): Flow<OneCallEntity?>

    @Query("SELECT * from  simple_weather WHERE id=:geoPoint")
    abstract suspend fun getCurrentResponse(geoPoint: String): SimpleWeatherEntity?

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveCurrentResponse(response: SimpleWeatherEntity)

}