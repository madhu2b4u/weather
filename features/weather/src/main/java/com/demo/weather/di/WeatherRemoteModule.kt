package com.demo.weather.di

import com.demo.weather.data.service.WeatherApiService
import com.demo.weather.data.source.WeatherInfoRemoteDataSource
import com.demo.weather.data.source.WeatherInfoRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module(includes = [WeatherRemoteModule.Binders::class])
@InstallIn(SingletonComponent::class)
class WeatherRemoteModule {
    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {
        @Binds
        fun bindsRemoteSource(
            remoteDataSourceImpl: WeatherInfoRemoteDataSourceImpl
        ): WeatherInfoRemoteDataSource
    }

    @Provides
    fun provideService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)
}