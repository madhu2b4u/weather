package com.demo.weather.database

import com.demo.core.database.DbWeather
import com.demo.core.database.Mapper
import com.demo.core.weather.model.WeatherInfo
import com.google.gson.Gson
import javax.inject.Inject

class WeatherMapper @Inject constructor(private val gson: Gson) : Mapper<DbWeather, WeatherInfo> {

    override fun from(e: WeatherInfo) = DbWeather(1, gson.toJson(e))

    override fun to(t: DbWeather): WeatherInfo {
        return gson.fromJson(
            t.weatherInfo,
            WeatherInfo::class.java
        )
    }
}