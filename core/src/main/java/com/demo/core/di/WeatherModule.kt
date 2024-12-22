package com.demo.core.di

import android.app.Application
import com.demo.core.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module()
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Provides
    @Singleton
    fun providesDatabase(
        application: Application
    ) = WeatherDatabase.getInstance(application.applicationContext)

    @Provides
    @Singleton
    fun providesDao(
        data: WeatherDatabase
    ) = data.getWeatherDao()
}