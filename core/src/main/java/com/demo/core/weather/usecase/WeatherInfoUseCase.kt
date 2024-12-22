package com.demo.core.weather.usecase

import com.demo.core.di.Result
import com.demo.core.weather.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherInfoUseCase {

    suspend fun getWeatherInfo(city: String?): Flow<Result<WeatherInfo>>

}