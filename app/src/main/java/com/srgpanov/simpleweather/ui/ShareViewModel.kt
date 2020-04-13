package com.srgpanov.simpleweather.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.data.models.weather.WeatherResponse
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShareViewModel: ViewModel(){
    var request: WeatherResponse?=null
    var daySelected: Int=-1
    var featureMember = MutableLiveData<FeatureMember>()
    val repository:DataRepository =DataRepositoryImpl(App.instance)
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    fun savePlaceToHistory(placeEntity: PlaceEntity) {
        scope.launch {
            repository.savePlaceToHistory(placeEntity)
        }

    }
}