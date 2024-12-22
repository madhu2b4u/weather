package com.demo.weather.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.core.DataStoreManager
import com.demo.core.di.Result
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.usecase.WeatherInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val weatherInfoUseCase: WeatherInfoUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Loading)
    val uiState: StateFlow<WeatherScreenState> = _uiState.asStateFlow()

    init {
        fetchWeatherInfo()
    }

    fun fetchWeatherInfo() {
        viewModelScope.launch {
            // Get city directly
            val city = dataStoreManager.getCity()
            Log.e("cityName", city.toString())
            if (city != null) {
                weatherInfoUseCase.getWeatherInfo(city)
                    .onStart { _uiState.value = WeatherScreenState.Loading }
                    .catch { e ->
                        _uiState.value =
                            WeatherScreenState.Error("Failed to fetch weather: ${e.message}")
                    }
                    .collect { result ->
                        _uiState.value = when (result) {
                            is Result.Success -> WeatherScreenState.Success(result.data)
                            is Result.Error -> WeatherScreenState.Error(result.message)
                            is Result.Empty -> WeatherScreenState.Empty(
                                result.title,
                                result.message
                            )

                            is Result.Loading -> WeatherScreenState.Loading
                        }
                    }
            } else {
                _uiState.value = WeatherScreenState.Empty(
                    title = "No Weather Data",
                    message = "Please search for a city to see weather information"
                )
            }
        }
    }
}
