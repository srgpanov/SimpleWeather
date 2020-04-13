package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.arch.core.util.Function
import androidx.lifecycle.*
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

class FavoriteViewModel:ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    val repository:DataRepository=DataRepositoryImpl(App.instance)

    val favoritePlaces=MutableLiveData<List<PlaceEntity>>()
    val currentPlace=MutableLiveData<PlaceEntity>()
    val searchHistory= MutableLiveData<List<PlaceEntity>>()
    init {
        scope.launch {
            val placesList =repository.getFavoritePlaces()
            placesList.forEach {
                it.favorite=true
            }
            favoritePlaces.postValue(placesList)
            logD("placesList ${placesList.size}")
        }
        scope.launch {
            val place =repository.getCurrentPlace()
            place?.let{
                place.current=true
                currentPlace.postValue(place)
            }
            logD("currentPlace ${place?.cityTitle} ${place?.current}")
        }
        scope.launch {
            val history = repository.getSearchHistory()
            searchHistory.postValue(history)
        }
    }

    override fun onCleared() {
        logD("onCleared")
        super.onCleared()
    }

    fun renamePlace(placeEntity: PlaceEntity) {
        scope.launch {
            repository.saveFavoritePlace(placeEntity)
            favoritePlaces.postValue(repository.getFavoritePlaces())
        }
    }

    fun removeFavoritePlace(placeEntity: PlaceEntity) {
        scope.launch {
            repository.removeFavoritePlace(placeEntity)
            favoritePlaces.postValue(repository.getFavoritePlaces())
        }
    }
}