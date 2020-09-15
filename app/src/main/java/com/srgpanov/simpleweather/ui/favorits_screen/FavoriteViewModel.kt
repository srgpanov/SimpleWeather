package com.srgpanov.simpleweather.ui.favorits_screen

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.domain_logic.view_converters.FavoritesConverter
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.CurrentViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.FavoritesViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.MutableLiveDataKt
import com.srgpanov.simpleweather.other.format
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FavoriteViewModel @Inject constructor(
    private var repository: DataRepository,
    private val converter: FavoritesConverter,
    preferences: PreferencesStorage
) : ViewModel() {

    val favoritePlaces = MutableLiveDataKt<List<FavoritesViewItem>>(emptyList())
    val currentPlace = MutableLiveData<CurrentViewItem>()
    private var currentJob: Job? = null
    private var favoriteJob: Job? = null

    private val sp = preferences.provideSharedPref()
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        restartCurrentWeatherFlows()
        restartFavoriteWeatherFlows()
    }


    init {
        restartCurrentWeatherFlows()
        restartFavoriteWeatherFlows()
        sp.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    override fun onCleared() {
        sp.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    private fun restartFavoriteWeatherFlows() {
        favoriteJob?.cancel()
        favoriteJob = favoriteFlow()
            .onEach { favoritePlaces.value = it }
            .launchIn(viewModelScope)
    }

    private fun restartCurrentWeatherFlows() {
        currentJob?.cancel()
        currentJob = currentFlow()
            .onEach { currentPlace.value = it }
            .launchIn(viewModelScope)
    }

    fun renamePlace(placeViewItem: PlaceViewItem) {
        viewModelScope.launch {
            repository.renamePlace(placeViewItem)
        }
    }

    fun removeFavoritePlace(placeViewItem: PlaceViewItem) {
        viewModelScope.launch {
            repository.removeFavoritePlace(placeViewItem)
        }
    }

    fun placeFavoriteOrCurrent(placeViewItem: PlaceViewItem): Boolean {
        val id = placeViewItem.toPlaceId()
        if (currentPlace.value?.place?.toPlaceId() == id) return true
        favoritePlaces.value.forEach { favoritePlace ->
            if (favoritePlace.place.toPlaceId() == id) {
                return true
            }
        }
        return false
    }

    fun refreshWeatherFromDetail() {
        viewModelScope.launch {
            for (favorite in favoritePlaces.value) {
                launch { repository.getSimpleFreshWeather(favorite.place.toGeoPoint()) }
            }
            launch {
                currentPlace.value?.place?.toGeoPoint()?.let { repository.getFreshWeather(it) }
            }
        }
    }

    fun savePlaceToHistory(placeViewItem: PlaceViewItem) {
        viewModelScope.launch {
            repository.savePlace(placeViewItem)
            repository.savePlaceToHistory(placeViewItem)
        }
    }

    private fun favoriteFlow(): Flow<List<FavoritesViewItem>> = repository.getFavoritePlaces()
        .onEach(::loadWeatherInList)
        .map(::transformToPlaceViewItems)

    private suspend fun loadWeatherInList(placesList: List<PlaceViewItem>) =
        withContext(Dispatchers.Default) {
            for (item in placesList) {
                val fresh =
                    item.simpleWeather?.simpleWeatherResponse?.isFresh == true
                Log.d(
                    "FavoriteViewModel",
                    "loadWeatherInList: isFresh $fresh ${
                        item.simpleWeather?.simpleWeatherResponse?.timeStamp?.let {
                            Date(it).format()
                        }
                    }"
                )
                if (item.simpleWeather == null || !fresh) {
                    repository.getSimpleFreshWeather(item.toGeoPoint())
                }
            }
        }

    private suspend fun transformToPlaceViewItems(placesList: List<PlaceViewItem>): List<FavoritesViewItem> =
        withContext(Dispatchers.Default) {
            placesList.map(converter::transformFavorite)
        }


    private fun currentFlow(): Flow<CurrentViewItem> =
        repository
            .getCurrentPlaceFlow()
            .distinctUntilChanged()
            .map { converter.transformCurrent(it) }
            .flowOn(Dispatchers.IO)
}

