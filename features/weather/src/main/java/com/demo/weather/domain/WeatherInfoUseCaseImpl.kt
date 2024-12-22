package com.demo.weather.domain

import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.weather.data.repository.WeatherInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherInfoUseCaseImpl @Inject constructor(private val repository: WeatherInfoRepository) :
    WeatherInfoUseCase {
    override suspend fun getWeatherInfo(city: String?) = repository.getWeatherInfo(city)
}