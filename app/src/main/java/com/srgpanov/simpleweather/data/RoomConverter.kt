package com.srgpanov.simpleweather.data


import com.google.gson.Gson
import com.srgpanov.simpleweather.data.entity.places.FeatureMember

//class RoomConverter {
//    @TypeConverter
//    fun fromPlaces(featureMember: FeatureMember): String {
//        return Gson().toJson(featureMember)
//
//
//    }
//
//    @TypeConverter
//    fun toPlaces(data: String): FeatureMember {
//        return Gson().fromJson(data, FeatureMember::class.java)
//    }
//
//    @TypeConverter
//    fun fromWeather(weatherRequest: WeatherRequest?): String {
//        return Gson().toJson(weatherRequest)
//
//
//    }
//
//    @TypeConverter
//    fun toWeather(data: String): WeatherRequest? {
//        return Gson().fromJson(data, WeatherRequest::class.java)
//    }
//}