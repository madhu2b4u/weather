package com.demo.search.domain

import com.demo.search.data.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchUseCaseImpl @Inject constructor(private val repository: SearchRepository) :
    SearchUseCase {
    override suspend fun getSearchResults(query: String) = repository.getSearchResults(query)
}