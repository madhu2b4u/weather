package com.demo.weather.data.source

import com.demo.core.weather.model.WeatherInfo

interface WeatherInfoLocalDataSource {
    suspend fun getWeatherInfo(): WeatherInfo?
    suspend fun saveWeatherInfo(weatherInfo: WeatherInfo)
}