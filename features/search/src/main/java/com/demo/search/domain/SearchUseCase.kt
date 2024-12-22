package com.demo.search.domain

import com.demo.core.di.Result
import com.demo.core.weather.model.Location
import kotlinx.coroutines.flow.Flow

interface SearchUseCase {

    suspend fun getSearchResults(query: String): Flow<Result<MutableList<Location>>>
}