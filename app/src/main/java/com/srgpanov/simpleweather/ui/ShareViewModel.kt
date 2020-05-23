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
    val weatherPlace = SingleLiveEvent<PlaceEntity>()
    val refreshWeather=SingleLiveEvent<Unit>()
    var currentPlace=SingleLiveEvent<PlaceEntity?>()

}