package com.srgpanov.simpleweather.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.di.ViewModelFactory
import com.srgpanov.simpleweather.di.ViewModelKey
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteViewModel
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    internal abstract fun favoriteViewModel(viewModel: FavoriteViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SelectPlaceViewModel::class)
    internal abstract fun selectPlaceViewModel(viewModel: SelectPlaceViewModel): ViewModel


}