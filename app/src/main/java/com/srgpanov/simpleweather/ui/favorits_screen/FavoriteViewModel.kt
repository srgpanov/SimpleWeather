package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val repository:DataRepository=DataRepositoryImpl(App.instance)

    val favoritePlaces=MutableLiveData<List<PlaceEntity>>()
    val currentPlace=MutableLiveData<PlaceEntity>()
    init {
        scope.launch {
            val placesList =repository.getFavoritePlaces()
            favoritePlaces.postValue(placesList)
            logD("placesList ${placesList.size}")
        }
        scope.launch {
            val place =repository.getCurrentPlace()
            currentPlace.postValue(place)
            logD("currentPlace ${place?.cityTitle}")
        }
    }

    override fun onCleared() {
        logD("onCleared")
        super.onCleared()
    }
}