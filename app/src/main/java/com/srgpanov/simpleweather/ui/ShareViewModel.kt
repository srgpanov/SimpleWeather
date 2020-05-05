package com.srgpanov.simpleweather.ui

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.other.SingleLiveEvent
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import kotlinx.coroutines.*
import java.util.function.BinaryOperator
import kotlin.coroutines.CoroutineContext

class ShareViewModel: ViewModel(){
    var oneCallResponse: OneCallResponse?=null
    var daySelected: Int=-1
    private val repository =DataRepositoryImpl()
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)
    val weatherPlace = SingleLiveEvent<PlaceEntity>()
    val refreshWeather=SingleLiveEvent<Unit>()
    var currentPlace=SingleLiveEvent<PlaceEntity?>()
    var sharedPreferences:SharedPreferences=PreferenceManager.getDefaultSharedPreferences(App.instance)

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }

    fun savePlaceToHistory(placeEntity: PlaceEntity) {
        scope.launch {
            repository.savePlaceToHistory(placeEntity)
        }
    }

    fun savePreferences(locationIsCurrent:Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(SettingFragment.LOCATION_TYPE_IS_CURRENT,locationIsCurrent).apply()
    }
}