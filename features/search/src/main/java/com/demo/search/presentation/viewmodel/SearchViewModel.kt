package com.demo.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.core.DataStoreManager
import com.demo.core.di.Result
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.Location
import com.demo.core.weather.usecase.WeatherInfoUseCase
import com.demo.search.domain.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchScreenState {
    data object Idle : SearchScreenState()
    data object Loading : SearchScreenState()
    data object Empty : SearchScreenState()
    data class Success(val cities: List<Location>) : SearchScreenState()
    data class Error(val message: String) : SearchScreenState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val weatherUseCase: WeatherInfoUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchScreenState>(SearchScreenState.Idle)
    val searchState: StateFlow<SearchScreenState> = _searchState.asStateFlow()

    private val _weatherState =
        MutableStateFlow<WeatherScreenState>(WeatherScreenState.Empty("", ""))
    val weatherState: StateFlow<WeatherScreenState> = _weatherState.asStateFlow()

    private var selectedCity: String = ""

    private fun saveCityToDataStore(city: String) {
        viewModelScope.launch {
            dataStoreManager.saveCity(city)
        }
    }

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            searchUseCase.getSearchResults(cityName)
                .onStart { _searchState.value = SearchScreenState.Loading }
                .catch { e ->
                    _searchState.value = SearchScreenState.Error("Search failed: ${e.message}")
                }
                .collect { result ->
                    _searchState.value = when (result) {
                        is Result.Success -> {
                            val cities = result.data
                            if (cities.isNotEmpty()) {
                                selectedCity = cities.firstOrNull()?.name.orEmpty()
                                saveCityToDataStore(selectedCity)
                                fetchWeatherInfo()
                                SearchScreenState.Success(cities)
                            } else {
                                SearchScreenState.Empty
                            }
                        }

                        is Result.Error -> SearchScreenState.Error(result.message)
                        is Result.Loading -> SearchScreenState.Loading
                        is Result.Empty -> SearchScreenState.Idle
                    }
                }
        }
    }

    private fun fetchWeatherInfo() {
        if (selectedCity.isNotEmpty()) {
            viewModelScope.launch {
                weatherUseCase.getWeatherInfo(selectedCity)
                    .onStart { _weatherState.value = WeatherScreenState.Loading }
                    .catch { e ->
                        _weatherState.value =
                            WeatherScreenState.Error("Weather fetch failed: ${e.message}")
                    }
                    .collect { result ->
                        _weatherState.value = when (result) {
                            is Result.Success -> WeatherScreenState.Success(result.data)
                            is Result.Error -> WeatherScreenState.Error(result.message)
                            is Result.Loading -> WeatherScreenState.Loading
                            is Result.Empty -> WeatherScreenState.Empty("", "")
                        }
                    }
            }
        } else {
            _weatherState.value = WeatherScreenState.Empty("", "")
        }
    }
}