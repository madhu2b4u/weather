package com.demo.search.data.repository

import com.demo.core.di.Result
import com.demo.core.weather.model.Location
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun getSearchResults(query: String): Flow<Result<MutableList<Location>>>
}