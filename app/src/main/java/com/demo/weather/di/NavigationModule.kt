package com.demo.weather.di

import com.demo.core.navigation.NavigationProvider
import com.demo.search.presentation.nav.SearchNavigationProvider
import com.demo.weather.presentation.nav.WeatherNavigationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @IntoSet
    fun provideWeatherNavigationProvider(): NavigationProvider {
        return WeatherNavigationProvider()
    }

    @Provides
    @IntoSet
    fun provideSearchNavigationProvider(): NavigationProvider {
        return SearchNavigationProvider()
    }
}