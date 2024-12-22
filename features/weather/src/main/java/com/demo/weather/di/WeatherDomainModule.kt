package com.demo.weather.di

import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.weather.data.repository.WeatherInfoRepository
import com.demo.weather.data.repository.WeatherInfoRepositoryImpl
import com.demo.weather.domain.WeatherInfoUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherDomainModule {

    @Binds
    internal abstract fun bindRepository(
        repoImpl: WeatherInfoRepositoryImpl
    ): WeatherInfoRepository


    @Binds
    internal abstract fun bindsUseCase(
        useCaseImpl: WeatherInfoUseCaseImpl
    ): WeatherInfoUseCase

}