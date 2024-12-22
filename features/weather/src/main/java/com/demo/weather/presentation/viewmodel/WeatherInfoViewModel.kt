package com.demo.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val weatherInfoUseCase: WeatherInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherScreenState>(WeatherScreenState.Empty("", ""))
    val uiState: StateFlow<WeatherScreenState> = _uiState.asStateFlow()

    init {
        fetchWeatherInfo()
    }

    fun fetchWeatherInfo(city: String? = null) {
        viewModelScope.launch {
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
                        is Result.Empty -> WeatherScreenState.Empty(result.title, result.message)
                        is Result.Loading -> WeatherScreenState.Loading
                    }
                }
        }
    }
}
