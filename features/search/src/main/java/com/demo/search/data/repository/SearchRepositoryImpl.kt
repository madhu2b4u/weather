package com.demo.search.data.repository

import com.demo.core.di.Result
import com.demo.core.weather.model.Location
import com.demo.search.data.source.SearchRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SearchRepositoryImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource,
) : SearchRepository {

    override suspend fun getSearchResults(query: String): Flow<Result<MutableList<Location>>> =
        flow {
            emit(Result.loading())
            try {
                val searchResults = remoteDataSource.getSearchResults(query)
                emit(Result.success(searchResults.toMutableList()))
            } catch (exception: Exception) {
                // If an exception occurs, emit error state
                emit(Result.error(exception.message ?: "", null))
            }
        }
}
