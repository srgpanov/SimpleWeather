package com.srgpanov.simpleweather.di.module

import com.srgpanov.simpleweather.BuildConfig
import com.srgpanov.simpleweather.data.remote.IpToLocationService
import com.srgpanov.simpleweather.data.remote.PlacesService
import com.srgpanov.simpleweather.data.remote.ResponseResultAdapterFactory
import com.srgpanov.simpleweather.data.remote.WeatherService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class RemoteModule {

    companion object{
        private const val baseUrlWeather = "https://api.openweathermap.org/data/2.5/"
        private const val baseUrlPlaces = "https://geocode-maps.yandex.ru/"
        private const val baseUrlIpToLocation = "http://ip-api.com/"
    }
    @Singleton
    @Provides
    fun getInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
        }
        return loggingInterceptor
    }
    @Singleton
    @Provides
    fun getHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
    @Singleton
    @Provides
    fun createWeatherService(httpClient: OkHttpClient): WeatherService {
        return Retrofit.Builder()
            .baseUrl(baseUrlWeather)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResponseResultAdapterFactory())
            .build()
            .create(WeatherService::class.java)
    }
    @Singleton
    @Provides
    fun createPlacesService(httpClient: OkHttpClient): PlacesService {
        return Retrofit.Builder()
            .baseUrl(baseUrlPlaces)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResponseResultAdapterFactory())
            .build()
            .create(PlacesService::class.java)
    }
    @Singleton
    @Provides
    fun createIpToLocationService(httpClient: OkHttpClient): IpToLocationService {
        return Retrofit.Builder()
            .baseUrl(baseUrlIpToLocation)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResponseResultAdapterFactory())
            .build()
            .create(IpToLocationService::class.java)
    }
}
