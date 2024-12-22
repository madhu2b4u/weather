package com.demo.weather.data.repository

import com.demo.core.di.Result
import com.demo.core.weather.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherInfoRepository {

    suspend fun getWeatherInfo(city: String?): Flow<Result<WeatherInfo>>

}