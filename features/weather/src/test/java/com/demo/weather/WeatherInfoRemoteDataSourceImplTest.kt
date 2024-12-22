package com.demo.weather

import com.demo.core.exception.NoDataException
import com.demo.core.weather.model.Condition
import com.demo.core.weather.model.Current
import com.demo.core.weather.model.Location
import com.demo.core.weather.model.WeatherInfo
import com.demo.weather.data.service.WeatherApiService
import com.demo.weather.data.source.WeatherInfoRemoteDataSourceImpl
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherInfoRemoteDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var service: WeatherApiService
    private lateinit var remoteDataSource: WeatherInfoRemoteDataSourceImpl

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
        Dispatchers.setMain(testDispatcher)
        service = mockk()
        remoteDataSource = WeatherInfoRemoteDataSourceImpl(service, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getWeatherInfo returns weather data on successful response`() = runTest {
        // Arrange
        val city = "London"
        val response = CompletableDeferred(Response.success(mockWeatherInfo))
        coEvery { service.getWeatherInfo(city) } returns response

        // Act
        val result = remoteDataSource.getWeatherInfo(city)

        // Assert
        assertEquals(mockWeatherInfo, result)
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo throws NoDataException when response body is null`() = runTest {
        // Arrange
        val city = "London"
        val response = CompletableDeferred(Response.success<WeatherInfo>(null))
        coEvery { service.getWeatherInfo(city) } returns response

        // Act & Assert
        val exception = assertThrows<NoDataException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals("Response body is null", exception.message)
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo throws HttpException on HTTP error response`() = runTest {
        // Arrange
        val city = "London"
        val errorResponse = Response.error<WeatherInfo>(
            404,
            "Not Found".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val response = CompletableDeferred(errorResponse)
        coEvery { service.getWeatherInfo(city) } returns response

        // Act & Assert
        val exception = assertThrows<HttpException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals(404, exception.code())
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo throws IOException on network failure`() = runTest {
        // Arrange
        val city = "London"
        coEvery { service.getWeatherInfo(city) } throws IOException("Network unavailable")

        // Act & Assert
        val exception = assertThrows<IOException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals("Network error occurred: Network unavailable", exception.message)
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo handles malformed city name`() = runTest {
        // Arrange
        val city = "!@#$%^"
        val errorResponse = Response.error<WeatherInfo>(
            400,
            "Bad Request".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val response = CompletableDeferred(errorResponse)
        coEvery { service.getWeatherInfo(city) } returns response

        // Act & Assert
        val exception = assertThrows<HttpException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals(400, exception.code())
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo handles empty city name`() = runTest {
        // Arrange
        val city = ""
        val errorResponse = Response.error<WeatherInfo>(
            400,
            "Bad Request".toResponseBody("application/json".toMediaTypeOrNull())
        )
        val response = CompletableDeferred(errorResponse)
        coEvery { service.getWeatherInfo(city) } returns response

        // Act & Assert
        val exception = assertThrows<HttpException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals(400, exception.code())
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }

    @Test
    fun `getWeatherInfo handles timeout exception`() = runTest {
        // Arrange
        val city = "London"
        coEvery { service.getWeatherInfo(city) } throws IOException("timeout")

        // Act & Assert
        val exception = assertThrows<IOException> {
            remoteDataSource.getWeatherInfo(city)
        }
        assertEquals("Network error occurred: timeout", exception.message)
        coVerify(exactly = 1) { service.getWeatherInfo(city) }
    }
}