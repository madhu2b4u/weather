package com.demo.weather.di

import com.demo.weather.data.source.WeatherInfoLocalDataSource
import com.demo.weather.data.source.WeatherInfoLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [WeatherLocalModule.Binders::class])
@InstallIn(SingletonComponent::class)
class WeatherLocalModule {
    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {
        @Binds
        fun bindsLocalDataSource(
            localDataSourceImpl: WeatherInfoLocalDataSourceImpl
        ): WeatherInfoLocalDataSource
    }
}