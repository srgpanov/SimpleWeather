package com.srgpanov.simpleweather.data.remote

import com.srgpanov.simpleweather.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val baseUrlWeather = "https://api.openweathermap.org/data/2.5/"
    private const val baseUrlPlaces = "https://geocode-maps.yandex.ru/"
    private const val baseUrlIpToLocation = "http://ip-api.com/"
    private fun getInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor=HttpLoggingInterceptor();
        if (BuildConfig.DEBUG){
            loggingInterceptor.apply { level= HttpLoggingInterceptor.Level.BODY}
        }
        return loggingInterceptor;
    }
    private fun getHttpClient():OkHttpClient{
        return OkHttpClient.Builder()
                .addInterceptor(getInterceptor())
                .build()
        }


    private fun createRetrofit(baseUrl:String):Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResponseResultAdapterFactory())
            .build()
    }


    fun createWeatherService(): WeatherService {
        return createRetrofit(
            baseUrlWeather
        ).create(WeatherService::class.java)
    }
    fun createPlacesService(): PlacesService {
        return createRetrofit(
            baseUrlPlaces
        ).create(PlacesService::class.java)
    }
    fun createIpToLocationService():IpToLocationService{
        return createRetrofit(
            baseUrlIpToLocation
        ).create(IpToLocationService::class.java)
    }
}