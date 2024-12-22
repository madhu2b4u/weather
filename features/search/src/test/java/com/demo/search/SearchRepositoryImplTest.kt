package com.demo.search

import com.demo.core.di.Result
import com.demo.core.weather.model.Location
import com.demo.search.data.repository.SearchRepository
import com.demo.search.data.repository.SearchRepositoryImpl
import com.demo.search.data.source.SearchRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchRepositoryTest {

    private lateinit var searchRepository: SearchRepository
    private val remoteDataSource = mockk<SearchRemoteDataSource>()
    private val mockLocations = mutableListOf(
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

    @Before
    fun setUp() {
        searchRepository = SearchRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getSearchResults returns locations successfully`() = runTest {
        // Arrange
        val query = "London"
        coEvery { remoteDataSource.getSearchResults(query) } returns mockLocations

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Success)
        assertEquals(mockLocations, (flowResults[1] as Result.Success).data)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }

    @Test
    fun `getSearchResults emits error when exception occurs`() = runTest {
        // Arrange
        val query = "London"
        val errorMessage = "Failed to fetch locations"
        coEvery { remoteDataSource.getSearchResults(query) } throws Exception(errorMessage)

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        val errorResult = flowResults[1] as Result.Error
        assertEquals(errorMessage, errorResult.message)
        assertEquals(null, errorResult.data)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }

   /* @Test
    fun `getSearchResults emits empty for empty response`() = runTest {
        // Arrange
        val query = "NonExistentLocation"
        coEvery { remoteDataSource.getSearchResults(query) } returns mutableListOf()

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Empty)
        val emptyResult = flowResults[1] as Result.Empty
        assertEquals("No Results", emptyResult.title)
        assertEquals("No locations found for your search", emptyResult.message)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }*/

    @Test
    fun `getSearchResults emits error on network failure`() = runTest {
        // Arrange
        val query = "London"
        val errorMessage = "Network error occurred"
        coEvery { remoteDataSource.getSearchResults(query) } throws java.io.IOException(errorMessage)

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        val errorResult = flowResults[1] as Result.Error
        assertEquals(errorMessage, errorResult.message)
        assertEquals(null, errorResult.data)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }

    @Test
    fun `getSearchResults returns multiple locations successfully`() = runTest {
        // Arrange
        val query = "London"
        coEvery { remoteDataSource.getSearchResults(query) } returns mockLocations

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Success)
        val successResult = flowResults[1] as Result.Success
        assertEquals(mockLocations, successResult.data)
        assertEquals(2, successResult.data.size)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }

    @Test
    fun `getSearchResults emits error with data when partial failure occurs`() = runTest {
        // Arrange
        val query = "London"
        val partialData = mutableListOf(mockLocations.first())
        val errorMessage = "Partial data fetched"
        coEvery { remoteDataSource.getSearchResults(query) } throws Exception(errorMessage)

        // Act
        val flowResults = searchRepository.getSearchResults(query).toList()

        // Assert
        assertEquals(2, flowResults.size)
        assertTrue(flowResults[0] is Result.Loading)
        assertTrue(flowResults[1] is Result.Error)
        val errorResult = flowResults[1] as Result.Error
        assertEquals(errorMessage, errorResult.message)
        coVerify(exactly = 1) { remoteDataSource.getSearchResults(query) }
    }
}