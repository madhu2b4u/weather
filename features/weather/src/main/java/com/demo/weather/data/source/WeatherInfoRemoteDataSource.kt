package com.demo.weather.data.source

import com.demo.core.weather.model.WeatherInfo

interface WeatherInfoRemoteDataSource {

    suspend fun getWeatherInfo(city: String): WeatherInfo

}