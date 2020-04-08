package com.srgpanov.simpleweather.data.remote

import android.util.Log
import com.facebook.stetho.okhttp.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.srgpanov.simpleweather.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val baseUrlWeather = "https://api.weather.yandex.ru/v1/"
    private const val baseUrlPlaces = "https://geocode-maps.yandex.ru/"
    private fun getInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor=HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d("Retrofit",message)
            }
        });
        if (BuildConfig.DEBUG){
            loggingInterceptor.apply { level= HttpLoggingInterceptor.Level.BODY}
        }
        return loggingInterceptor;
    }
    private fun getHttpClient(isWeather:Boolean):OkHttpClient{
        return when (isWeather){
            true -> OkHttpClient.Builder()
                .addInterceptor {
                    val original = it.request()
                    val request = original.newBuilder()
                        .addHeader("X-Yandex-API-Key", "098f7b10-efc0-48af-8fbe-a8f62507cb99")
                        .build()
                    it.proceed(request)
                }
                .addInterceptor(getInterceptor())
                .build()
            false -> OkHttpClient.Builder()
                .addInterceptor(getInterceptor())
                .build()
        }}


    private fun createRetrofit(baseUrl:String, httpClient:OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }


    fun createWeatherService(): WeatherService {
        return createRetrofit(
            baseUrlWeather,
            getHttpClient(
                true
            )
        ).create(WeatherService::class.java)
    }
    fun createPlacesService(): PlacesService {
        return createRetrofit(
            baseUrlPlaces,
            getHttpClient(
                false
            )
        ).create(PlacesService::class.java)
    }
}