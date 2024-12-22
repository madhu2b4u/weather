package com.demo.core.weather

import com.demo.core.weather.model.WeatherInfo

sealed class WeatherScreenState {
    data object Loading : WeatherScreenState()
    data class Success(val weatherInfo: WeatherInfo) : WeatherScreenState()
    data class Error(val message: String) : WeatherScreenState()
    data class Empty(val title: String, val message: String) : WeatherScreenState()
}
