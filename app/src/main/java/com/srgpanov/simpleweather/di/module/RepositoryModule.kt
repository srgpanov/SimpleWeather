package com.srgpanov.simpleweather.di.module

import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun getRepository(local: LocalDataSourceImpl,remote:RemoteDataSourceImpl):DataRepository{
        return DataRepository(local,remote)
    }

}