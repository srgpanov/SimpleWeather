package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.data.models.places.Places
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("1.x/")
    suspend fun getPlaces(
        @Query("geocode") geocode: String,
        @Query("format") format: String="json",
        @Query("lang") lang: String = "ru_RU",
        @Query("apikey") apikey: String ,
        @Query("kind") kind: String="locality" ,
        @Query("results") results: Int = 15
    ): ResponseResult<Places>
}