package com.demo.search.data.source

import com.demo.core.weather.model.Location

interface SearchRemoteDataSource {

    suspend fun getSearchResults(query: String): MutableList<Location>
}