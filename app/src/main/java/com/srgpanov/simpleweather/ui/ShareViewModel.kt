package com.srgpanov.simpleweather.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srgpanov.simpleweather.data.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.entity.PlaceEntity
import com.srgpanov.simpleweather.data.entity.places.FeatureMember
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ShareViewModel: ViewModel(){
    var request: WeatherResponse?=null
    var daySelected: Int=-1
    var featureMember = MutableLiveData<FeatureMember>()
    val localDataSourceImpl=LocalDataSourceImpl()

    fun savePlace(placeEntity: PlaceEntity){
        CoroutineScope(Dispatchers.IO).launch {

            localDataSourceImpl.dao.savePlace(placeEntity)
            logD("saved")
        }
    }

}