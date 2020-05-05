package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.entity.SimpleWeatherTable
import com.srgpanov.simpleweather.data.models.weather.current_weather.CurrentWeatherResponse
import com.srgpanov.simpleweather.data.remote.ResponseResult
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.logE
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FavoriteViewModel : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    val repository = DataRepositoryImpl()

    val favoritePlaces = MutableLiveData<List<PlaceEntity>>()
    val currentPlace = MutableLiveData<PlaceEntity>()
    val searchHistory = MutableLiveData<List<PlaceEntity>>()
    var searchViewOpen = false


    fun refreshPlaces() {
        scope.launch {
            val placesList = repository.getFavoritePlaces()
            placesList.forEach {
                it.favorite = true
            }
            favoritePlaces.postValue(placesList)
            loadWeather(placesList)
            logD("placesList ${placesList.size}")
        }
//        scope.launch {
//            val place = repository.getCurrentPlace()
//            place?.let {
//                place.current = true
//                currentPlace.postValue(place)
//                loadMainWeather(it)
//            }
//            logD("currentPlace ${place?.cityTitle} ${place?.current}")
//        }
        scope.launch {
            val history = repository.getSearchHistory()
            searchHistory.postValue(history)
        }
    }


    override fun onCleared() {
        logD("onCleared")
        scope.cancel()
        super.onCleared()
    }

    fun renamePlace(placeEntity: PlaceEntity) {
        scope.launch {
            repository.saveFavoritePlace(placeEntity)
            val places = repository.getFavoritePlaces()
            favoritePlaces.postValue(places)
            loadWeather(places)
        }
    }

    fun removeFavoritePlace(placeEntity: PlaceEntity) {
        scope.launch {
            repository.removeFavoritePlace(placeEntity)
            val places = repository.getFavoritePlaces()
            favoritePlaces.postValue(places)
            loadWeather(places)
        }
    }

    fun placeFavoriteOrCurrent(placeEntity: PlaceEntity): Boolean {
        val id = placeEntity.toPlaceId()
        if (currentPlace.value?.toPlaceId() == id) return true
        favoritePlaces.value?.forEach {
            if (it.toPlaceId() == id) {
                return true
            }
        }
        return false
    }

    fun refreshWeather(placeList: List<PlaceEntity>? = favoritePlaces.value) {
        if (placeList != null) {
            loadWeather(placeList, true)
        }
//        loadMainWeather()
    }

    fun loadWeather(placeList: List<PlaceEntity>? = favoritePlaces.value) {
        if (placeList != null) {
            loadWeather(placeList, false)
        }
    }

    private fun loadWeather(placeList: List<PlaceEntity>, fresh: Boolean) {
        logD("deffered loadWeather ${placeList.size}")
        scope.launch {
            val deferredList = mutableListOf<Deferred<ResponseResult<CurrentWeatherResponse>>>()
            placeList.forEach {
                val async = async {
                    if (fresh) {
                        repository.getSimpleFreshWeather(it.toGeoPoint())
                    } else {
                        repository.getSimpleWeather(it.toGeoPoint())
                    }
                }
                deferredList.add(async)
            }
            deferredList.forEachIndexed { index, deferred ->
                val result = deferred.await()
                when (result) {
                    is ResponseResult.Success -> {
                        placeList[index].simpleWeatherTable =
                            SimpleWeatherTable(result.data.getGeoPoint().pointToId(), result.data)
                    }
                    is ResponseResult.Failure.ServerError ->logE("ServerError errorCode ${result.errorCode} errorBody ${result.errorBody}")
                    is ResponseResult.Failure.NetworkError ->logE("NetworkError ex ${result.ex}")
                }

            }
            favoritePlaces.postValue(placeList)
        }
    }

//    private fun loadMainWeather(place: PlaceEntity? = currentPlace.value) {
//        scope.launch {
//            if (place != null) {
//                val weather = repository.getWeather(place.toGeoPoint())
//                place.oneCallResponse = weather
//                currentPlace.postValue(place)
//            }
//        }
//    }
}