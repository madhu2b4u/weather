package com.demo.search

import com.demo.core.exception.NoDataException
import com.demo.core.weather.model.Location
import com.demo.search.data.service.SearchApiService
import com.demo.search.data.source.SearchRemoteDataSourceImpl
import com.google.gson.Gson
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
import java.io.InputStreamReader

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRemoteDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var service: SearchApiService
    private lateinit var searchDataSource: SearchRemoteDataSourceImpl
    private val gson = Gson()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        service = mockk()
        searchDataSource = SearchRemoteDataSourceImpl(service, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun loadJson(filename: String): String {
        val inputStream = javaClass.classLoader!!.getResourceAsStream("raw/$filename")
            ?: throw IllegalArgumentException("File not found: $filename")
        return InputStreamReader(inputStream).use { it.readText() }
    }


    // Helper function to create a Retrofit Response
    private fun <T> createResponse(body: T?, isSuccessful: Boolean = true): Response<T> {
        return if (isSuccessful) {
            Response.success(body)
        } else {
            Response.error(400, "Error".toResponseBody("application/json".toMediaTypeOrNull()))
        }
    }

    @Test
    fun `getLocation returns list of cities on successful response`() = runTest {
        // Arrange
        val searchJson = loadJson("search_api.json")
        val searchResponse = gson.fromJson(searchJson, mutableListOf<Location>()::class.java)
        coEvery { service.searchLocation("Hyderabad") } returns CompletableDeferred(
            createResponse(
                searchResponse
            )
        )

        // Act
        val result = searchDataSource.getSearchResults("Hyderabad")

        // Assert
        assertEquals(searchResponse, result)
        coVerify(exactly = 1) { service.searchLocation("Hyderabad") }
    }


    @Test
    fun `getSearchResults returns location list on successful response`() = runTest {
        // Arrange
        val query = "London"
        val expectedLocations = mutableListOf(
            Location(
                country = "UK",
                lat = 51.5074,
                lon = -0.1278,
                name = "London",
                region = "Greater London"
            )
        )
        val response = CompletableDeferred(createResponse(expectedLocations))
        coEvery { service.searchLocation(query) } returns response

        // Act
        val result = searchDataSource.getSearchResults(query)

        // Assert
        assertEquals(expectedLocations, result)
        coVerify(exactly = 1) { service.searchLocation(query) }
    }

    @Test
    fun `getSearchResults throws HttpException on HTTP error response`() = runTest {
        // Arrange
        val query = "London"
        val errorResponse = CompletableDeferred(
            Response.error<MutableList<Location>>(404, "Not Found".toResponseBody(null))
        )
        coEvery { service.searchLocation(query) } returns errorResponse

        // Act & Assert
        val exception = assertThrows<HttpException> {
            searchDataSource.getSearchResults(query)
        }
        assertEquals(404, exception.code())
        coVerify(exactly = 1) { service.searchLocation(query) }
    }

    @Test
    fun `getSearchResults throws NoDataException when response body is null`() = runTest {
        // Arrange
        val query = "London"
        val response = Response.success<MutableList<Location>>(null)
        val deferred = CompletableDeferred(response)
        coEvery { service.searchLocation(query) } returns deferred

        // Act & Assert
        val exception = assertThrows<NoDataException> {
            searchDataSource.getSearchResults(query)
        }
        assertEquals("Response body is null", exception.message)
        coVerify(exactly = 1) { service.searchLocation(query) }
    }

    @Test
    fun `getSearchResults throws IOException on network failure`() = runTest {
        // Arrange
        val query = "London"
        coEvery { service.searchLocation(query) } throws IOException("Network Failure")

        // Act & Assert
        val exception = assertThrows<IOException> {
            searchDataSource.getSearchResults(query)
        }
        assertEquals("Network error occurred: Network Failure", exception.message)
        coVerify(exactly = 1) { service.searchLocation(query) }
    }

    @Test
    fun `getSearchResults returns empty list for empty query`() = runTest {
        // Arrange
        val query = ""
        val emptyLocations = mutableListOf<Location>()
        val response = CompletableDeferred(createResponse(emptyLocations))
        coEvery { service.searchLocation(query) } returns response

        // Act
        val result = searchDataSource.getSearchResults(query)

        // Assert
        assertEquals(emptyLocations, result)
        coVerify(exactly = 1) { service.searchLocation(query) }
    }

    @Test
    fun `getSearchResults handles multiple locations in response`() = runTest {
        // Arrange
        val query = "London"
        val expectedLocations = mutableListOf(
            Location(
                country = "UK",
                lat = 51.5074,
                lon = -0.1278,
                name = "London",
                region = "Greater London"
            ),
            Location(
                country = "Canada",
                lat = 42.9849,
                lon = -81.2453,
                name = "London",
                region = "Ontario"
            )
        )
        val response = CompletableDeferred(createResponse(expectedLocations))
        coEvery { service.searchLocation(query) } returns response

        // Act
        val result = searchDataSource.getSearchResults(query)

        // Assert
        assertEquals(expectedLocations, result)
        assertEquals(2, result.size)
        coVerify(exactly = 1) { service.searchLocation(query) }
    }
}