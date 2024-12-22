package com.demo.search.data.service

import com.demo.core.di.API_KEY
import com.demo.core.weather.model.Location
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("v1/search.json")
    fun searchLocation(
        @Query("q") query: String,
        @Query("key") apiKey: String = API_KEY,
    ): Deferred<Response<MutableList<Location>>>
}