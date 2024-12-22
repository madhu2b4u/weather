package com.demo.search.data.source

import com.demo.core.di.qualifiers.IO
import com.demo.core.exception.NoDataException
import com.demo.search.data.service.SearchApiService
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchRemoteDataSourceImpl @Inject constructor(
    private val service: SearchApiService,
    @IO private val context: CoroutineContext
) : SearchRemoteDataSource {
    override suspend fun getSearchResults(query: String) = withContext(context) {
        try {
            val response = service.searchLocation(query).await()
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
