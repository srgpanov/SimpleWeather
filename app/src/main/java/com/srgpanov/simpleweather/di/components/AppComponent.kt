package com.srgpanov.simpleweather.di.components

import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.di.module.*
import com.srgpanov.simpleweather.other.LocationProvider
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidgetSettingListFragment
import com.srgpanov.simpleweather.ui.weather_widget.WidgetUpdateHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    RemoteModule::class,
    LocalModule::class,
    RepositoryModule::class,
    ViewModelsModule::class])
interface AppComponent {
    fun injectDetailFragment(fragment: DetailFragment)
    fun injectFavoriteFragment(fragment: FavoriteFragment)
    fun injectWidgetSettingsListFragment(fragment: WeatherWidgetSettingListFragment)
    fun injectSelectPlaceFragment(fragment: SelectPlaceFragment)
    fun injectLocationProvider(provider: LocationProvider)
    fun getRepository(): DataRepository
    fun getWidgetHelper(): WidgetUpdateHelper

}