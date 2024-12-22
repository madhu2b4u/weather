package com.demo.weather.data.source

import com.demo.core.database.WeatherDao
import com.demo.core.di.qualifiers.IO
import com.demo.core.weather.model.WeatherInfo
import com.demo.weather.database.WeatherMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WeatherInfoLocalDataSourceImpl @Inject constructor(
    private val dao: WeatherDao,
    private val mapper: WeatherMapper,
    @IO private val context: CoroutineContext
) : WeatherInfoLocalDataSource {

    override suspend fun getWeatherInfo() = withContext(context) {
        val entity = dao.getWeatherFromDatabase()
        if (entity != null)
            mapper.to(entity)
        else
            null
    }

    override suspend fun saveWeatherInfo(weatherInfo: WeatherInfo) {
        withContext(context) {
            val weatherInfo = mapper.from(weatherInfo)
            dao.saveWeatherToDatabase(weatherInfo)
        }
    }
}