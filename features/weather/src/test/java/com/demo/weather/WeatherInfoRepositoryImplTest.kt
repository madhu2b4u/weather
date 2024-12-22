package com.demo.weather

import com.demo.core.di.Result
import com.demo.core.weather.model.Condition
import com.demo.core.weather.model.Current
import com.demo.core.weather.model.WeatherInfo
import com.demo.core.weather.model.Location
import com.demo.weather.data.repository.WeatherInfoRepositoryImpl
import com.demo.weather.data.source.WeatherInfoLocalDataSource
import com.demo.weather.data.source.WeatherInfoRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherInfoRepositoryImplTest {

    private lateinit var repository: WeatherInfoRepositoryImpl
    private val remoteDataSource = mockk<WeatherInfoRemoteDataSource>()
    private val localDataSource = mockk<WeatherInfoLocalDataSource>()

    private val mockWeatherInfo = WeatherInfo(
        current = Current(
            condition = Condition(
                code = 1000,
                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                text = "Sunny"
            ),
            feelslike_c = 25.0,
            humidity = 65,
            temp_c = 24.0,
            uv = 6.0
        ),
        location = Location(
            country = "UK",
            lat = 51.5074,
            lon = -0.1278,
            name = "London",
            region = "Greater London"
        )
    )

    @Before
    fun setUp() {
        repository = WeatherInfoRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getWeatherInfo with city fetches from remote and caches`() = runTest {
        // Arrange
        val city = "London"
        coEvery { remoteDataSource.getWeatherInfo(city) } returns mockWeatherInfo
        coEvery { localDataSource.saveWeatherInfo(mockWeatherInfo) } returns Unit

        // Act
        val flowResults = repository.getWeatherInfo(city).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Success)
        assertEquals(mockWeatherInfo, (flowResults[1] as Result.Success).data)
        
        coVerify(exactly = 1) {
            remoteDataSource.getWeatherInfo(city)
            localDataSource.saveWeatherInfo(mockWeatherInfo)
        }
    }

    @Test
    fun `getWeatherInfo without city returns cached data`() = runTest {
        // Arrange
        coEvery { localDataSource.getWeatherInfo() } returns mockWeatherInfo

        // Act
        val flowResults = repository.getWeatherInfo(null).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Success)
        assertEquals(mockWeatherInfo, (flowResults[1] as Result.Success).data)
        
        coVerify(exactly = 1) { localDataSource.getWeatherInfo() }
        coVerify(exactly = 0) { remoteDataSource.getWeatherInfo(any()) }
    }

    @Test
    fun `getWeatherInfo without city and empty cache returns Empty result`() = runTest {
        // Arrange
        coEvery { localDataSource.getWeatherInfo() } returns null

        // Act
        val flowResults = repository.getWeatherInfo(null).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Empty)
        val emptyResult = flowResults[1] as Result.Empty
        assertEquals("No Weather Data", emptyResult.title)
        assertEquals("Please search for a city to see weather information", emptyResult.message)
        
        coVerify(exactly = 1) { localDataSource.getWeatherInfo() }
        coVerify(exactly = 0) { remoteDataSource.getWeatherInfo(any()) }
    }

    @Test
    fun `getWeatherInfo with city handles remote error`() = runTest {
        // Arrange
        val city = "London"
        val errorMessage = "Network error occurred"
        coEvery { remoteDataSource.getWeatherInfo(city) } throws Exception(errorMessage)

        // Act
        val flowResults = repository.getWeatherInfo(city).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        assertEquals(errorMessage, (flowResults[1] as Result.Error).message)
        
        coVerify(exactly = 1) { remoteDataSource.getWeatherInfo(city) }
        coVerify(exactly = 0) { localDataSource.saveWeatherInfo(any()) }
    }

    @Test
    fun `getWeatherInfo without city handles local error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { localDataSource.getWeatherInfo() } throws Exception(errorMessage)

        // Act
        val flowResults = repository.getWeatherInfo(null).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        assertEquals(errorMessage, (flowResults[1] as Result.Error).message)
        
        coVerify(exactly = 1) { localDataSource.getWeatherInfo() }
        coVerify(exactly = 0) { remoteDataSource.getWeatherInfo(any()) }
    }

    @Test
    fun `getWeatherInfo with city handles caching error`() = runTest {
        // Arrange
        val city = "London"
        val errorMessage = "Cache error"
        coEvery { remoteDataSource.getWeatherInfo(city) } returns mockWeatherInfo
        coEvery { localDataSource.saveWeatherInfo(mockWeatherInfo) } throws Exception(errorMessage)

        // Act
        val flowResults = repository.getWeatherInfo(city).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        assertEquals(errorMessage, (flowResults[1] as Result.Error).message)
        
        coVerify(exactly = 1) {
            remoteDataSource.getWeatherInfo(city)
            localDataSource.saveWeatherInfo(mockWeatherInfo)
        }
    }

    @Test
    fun `getWeatherInfo handles unexpected error with default message`() = runTest {
        // Arrange
        val city = "London"
        coEvery { remoteDataSource.getWeatherInfo(city) } throws Exception()

        // Act
        val flowResults = repository.getWeatherInfo(city).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        assertEquals("An unexpected error occurred", (flowResults[1] as Result.Error).message)
        
        coVerify(exactly = 1) { remoteDataSource.getWeatherInfo(city) }
    }
}