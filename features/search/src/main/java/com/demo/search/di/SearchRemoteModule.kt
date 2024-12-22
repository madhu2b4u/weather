package com.demo.search.di

import com.demo.search.data.service.SearchApiService
import com.demo.search.data.source.SearchRemoteDataSource
import com.demo.search.data.source.SearchRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module(includes = [SearchRemoteModule.Binders::class])
@InstallIn(SingletonComponent::class)
class SearchRemoteModule {
    @Module
    @InstallIn(SingletonComponent::class)
    interface Binders {
        @Binds
        fun bindsRemoteSource(
            remoteDataSourceImpl: SearchRemoteDataSourceImpl
        ): SearchRemoteDataSource
    }

    @Provides
    fun provideService(retrofit: Retrofit): SearchApiService =
        retrofit.create(SearchApiService::class.java)
}