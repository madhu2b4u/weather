package com.demo.weather.data.repository

import com.demo.core.di.Result
import com.demo.core.weather.model.WeatherInfo
import com.demo.weather.data.source.WeatherInfoLocalDataSource
import com.demo.weather.data.source.WeatherInfoRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherInfoRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherInfoRemoteDataSource,
    private val localDataSource: WeatherInfoLocalDataSource,
) : WeatherInfoRepository {
    override suspend fun getWeatherInfo(city: String?): Flow<Result<WeatherInfo>> = flow {
        emit(Result.Loading)

        try {
            when {
                // If city is provided, fetch from remote and cache
                city != null -> {
                    val remoteData = remoteDataSource.getWeatherInfo(city)
                    localDataSource.saveWeatherInfo(remoteData)
                    emit(Result.Success(remoteData))
                }
                // If no city provided, fetch from local cache
                else -> {
                    val localData = localDataSource.getWeatherInfo()
                    if (localData != null) {
                        emit(Result.Success(localData))
                    } else {
                        emit(Result.Empty(
                            title = "No Weather Data",
                            message = "Please search for a city to see weather information"
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }.catch { e ->
        emit(Result.Error(e.message ?: "An unexpected error occurred"))
    }
}
