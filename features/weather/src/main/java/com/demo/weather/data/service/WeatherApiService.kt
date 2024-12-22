package com.demo.weather.data.service

import com.demo.core.di.API_KEY
import com.demo.core.weather.model.WeatherInfo
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/current.json")
    fun getWeatherInfo(
        @Query("q") location: String,
        @Query("key") apiKey: String = API_KEY,
    ): Deferred<Response<WeatherInfo>>
}