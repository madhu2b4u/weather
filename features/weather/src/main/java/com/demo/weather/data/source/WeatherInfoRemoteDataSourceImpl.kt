package com.demo.weather.data.source

import com.demo.core.di.qualifiers.IO
import com.demo.core.exception.NoDataException
import com.demo.weather.data.service.WeatherApiService
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WeatherInfoRemoteDataSourceImpl @Inject constructor(
    private val service: WeatherApiService,
    @IO private val context: CoroutineContext
) : WeatherInfoRemoteDataSource {

    override suspend fun getWeatherInfo(city: String) = withContext(context) {
        try {
            val response = service.getWeatherInfo(city).await()
            if (response.isSuccessful) {
                response.body() ?: throw NoDataException("Response body is null")
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw IOException("Network error occurred: ${e.message}", e)
        }
    }
}
