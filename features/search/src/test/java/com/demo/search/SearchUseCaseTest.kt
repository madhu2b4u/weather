package com.demo.search

import com.demo.core.di.Result
import com.demo.core.weather.model.Location
import com.demo.search.data.repository.SearchRepository
import com.demo.search.domain.SearchUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchUseCaseTest {

    private lateinit var searchUseCase: SearchUseCaseImpl
    private val repository = mockk<SearchRepository>()

    @Before
    fun setUp() {
        searchUseCase = SearchUseCaseImpl(repository)
    }

    @Test
    fun `getSearchResults calls repository and returns success result when locations are loaded`() =
        runTest {
            // Arrange
            val locations = mutableListOf(
                Location(
                    country = "New Zealand",
                    lat = -36.8485,
                    lon = 174.7633,
                    name = "Auckland",
                    region = "Auckland"
                ),
                Location(
                    country = "Australia",
                    lat = -33.8688,
                    lon = 151.2093,
                    name = "Sydney",
                    region = "New South Wales"
                )
            )
            val expectedResult = Result.success(locations)

            coEvery { repository.getSearchResults("Auckland") } returns flow { emit(expectedResult) }

            // Act
            val result = searchUseCase.getSearchResults("Auckland")

            // Assert
            result.collect { res ->
                assertEquals(expectedResult, res)
            }
            coVerify(exactly = 1) { repository.getSearchResults("Auckland") }
        }

    @Test
    fun `getSearchResults calls repository and returns error result when an exception occurs`() =
        runTest {
            // Arrange
            val errorMessage = "Failed to fetch locations"
            val expectedResult = Result.error<MutableList<Location>>(errorMessage, null)

            coEvery { repository.getSearchResults("Auckland") } returns flow { emit(expectedResult) }

            // Act
            val result = searchUseCase.getSearchResults("Auckland")

            // Assert
            result.collect { res ->
                assertEquals(expectedResult, res)
            }
            coVerify(exactly = 1) { repository.getSearchResults("Auckland") }
        }
}