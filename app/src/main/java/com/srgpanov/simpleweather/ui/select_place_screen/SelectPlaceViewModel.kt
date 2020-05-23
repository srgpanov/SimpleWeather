package com.srgpanov.simpleweather.ui.select_place_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SelectPlaceViewModel :ViewModel(){
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    val repository = DataRepositoryImpl
    val searchHistory = MutableLiveData<List<PlaceEntity>>()
    init {
        scope.launch {
            val history = repository.getSearchHistory()
            searchHistory.postValue(history)
        }

    }
    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}