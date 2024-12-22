package com.demo.search.di

import com.demo.search.data.repository.SearchRepository
import com.demo.search.data.repository.SearchRepositoryImpl
import com.demo.search.domain.SearchUseCase
import com.demo.search.domain.SearchUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDomainModule {
    @Binds
    internal abstract fun bindRepository(
        repoImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    internal abstract fun bindsUseCase(
        useCaseImpl: SearchUseCaseImpl
    ): SearchUseCase

}