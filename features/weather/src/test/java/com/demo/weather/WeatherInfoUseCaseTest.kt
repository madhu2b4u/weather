package com.demo.weather

import com.demo.core.di.Result
import com.demo.core.weather.model.Condition
import com.demo.core.weather.model.Current
import com.demo.core.weather.model.Location
import com.demo.core.weather.model.WeatherInfo
import com.demo.weather.data.repository.WeatherInfoRepository
import com.demo.weather.domain.WeatherInfoUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherInfoUseCaseTest {

    private lateinit var weatherInfoUseCase: WeatherInfoUseCaseImpl
    private val repository = mockk<WeatherInfoRepository>()

    @Before
    fun setUp() {
        weatherInfoUseCase = WeatherInfoUseCaseImpl(repository)
    }

    @Test
    fun `getWeatherInfo calls repository and returns success result when weather data is loaded`() =
        runTest {
            // Arrange
            val weatherInfo = WeatherInfo(
                location = Location(
                    country = "New Zealand",
                    lat = -36.8485,
                    lon = 174.7633,
                    name = "Auckland",
                    region = "Auckland"
                ),
                current = Current(
                    condition = Condition(
                        code = 1000,
                        icon = "//cdn.weather.com/sunny.png",
                        text = "Sunny"
                    ),
                    feelslike_c = 25.0,
                    humidity = 65,
                    temp_c = 24.5,
                    uv = 6.5
                )
            )
            val expectedResult = Result.success(weatherInfo)

            coEvery { repository.getWeatherInfo("Auckland") } returns flow { emit(expectedResult) }

            // Act
            val result = weatherInfoUseCase.getWeatherInfo("Auckland")

            // Assert
            result.collect { res ->
                assertEquals(expectedResult, res)
            }
            coVerify(exactly = 1) { repository.getWeatherInfo("Auckland") }
        }

    @Test
    fun `getWeatherInfo calls repository and returns error result when an exception occurs`() =
        runTest {
            // Arrange
            val errorMessage = "Failed to fetch weather information"
            val expectedResult = Result.error<WeatherInfo>(errorMessage, null)

            coEvery { repository.getWeatherInfo("Auckland") } returns flow { emit(expectedResult) }

            // Act
            val result = weatherInfoUseCase.getWeatherInfo("Auckland")

            // Assert
            result.collect { res ->
                assertEquals(expectedResult, res)
            }
            coVerify(exactly = 1) { repository.getWeatherInfo("Auckland") }
        }
}