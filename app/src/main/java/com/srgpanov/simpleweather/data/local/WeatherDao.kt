package com.srgpanov.simpleweather.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.srgpanov.simpleweather.data.models.entity.*
import com.srgpanov.simpleweather.data.models.entity.utility.PlacesWithSimpleWeather
import com.srgpanov.simpleweather.data.models.entity.utility.PlacesWithWeather
import com.srgpanov.simpleweather.other.logD

@Dao
abstract class WeatherDao {

    @Transaction
    @Query("SELECT *  from PlaceTable, FavoriteTable WHERE id==idFavorite")
    abstract suspend fun getFavorites(): List<PlacesWithSimpleWeather>

    @Transaction
    @Query("SELECT * from PlaceTable,CurrentTable WHERE id==idCurrent")
    abstract suspend fun getCurrentLocation(): List<PlacesWithWeather>

    @Transaction
    @Query("SELECT * from PlaceTable,SearchHistoryTable WHERE id==idSearch ORDER BY timeStamp DESC ")
    abstract suspend fun getSearchHistory(): List<PlacesWithWeather>
    @Transaction
    @Query("SELECT * from PlaceTable WHERE id==:id")
    abstract suspend fun getPlaceById(id: String):PlacesWithWeather?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertPlace(placeTable: PlaceTable): Long

    @Update
    abstract suspend fun update(placeTable: PlaceTable)

    @Query("SELECT * from PlaceTable WHERE id=:point")
    abstract suspend fun placeIsInDb(point: String): PlaceTable?

    @Transaction
    open suspend fun insertOrUpdatePlace(placeTable: PlaceTable) {
        val id = insertPlace(placeTable)
        if (id == -1L) update(placeTable)
    }

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

//todo

//    @Transaction
//    open suspend fun savePlaceToHistoryMaxPlace(place: SearchHistoryTable, maxPlace: Int = 30) {
//        val placeList = getSearchHistory()
//        if (placeList.size >= maxPlace) {
//            placeList.forEachIndexed { index, placeEntity ->
//                if (index >= maxPlace) {
//                    logD("place deleted ${placeEntity.cityFullName}")
//                    deletePlaceFromHistory(placeEntity.toSearchHistoryTable())
//                }
//            }
//        }
//        savePlaceToHistory(place)
//    }


    @Delete
    abstract suspend fun deletePlaceFromHistory(place: SearchHistoryTable)

    @Query("SELECT * from FavoriteTable WHERE idFavorite=:pointToId")
    abstract suspend fun placeIsFavorite(pointToId: String): FavoriteTable?

    @Query("DELETE from FavoriteTable WHERE idFavorite=:id ")
    abstract suspend fun removeFavoritePlace(id: String): Int

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveFavoritePlace(place: FavoriteTable)

    @Insert(onConflict = REPLACE)
    abstract suspend fun saveOneCallResponse(weather: OneCallTable)

    @Query("SELECT * from OneCallTable")
    abstract suspend fun getLastResponse(): List<OneCallTable>

    @Query("SELECT * from  OneCallTable WHERE id=:geoPoint")
    abstract suspend fun getOneCallResponse(geoPoint: String): OneCallTable?
    @Query("SELECT * from  SimpleWeatherTable WHERE id=:geoPoint")
    abstract suspend fun getCurrentResponse(geoPoint: String): SimpleWeatherTable?
    @Insert(onConflict = REPLACE)
    abstract suspend fun saveCurrentResponse(response: SimpleWeatherTable)

}