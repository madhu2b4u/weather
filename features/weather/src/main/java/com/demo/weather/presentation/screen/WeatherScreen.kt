package com.demo.weather.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.demo.core.ui.font.FontProvider
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.WeatherInfo
import com.demo.weather.R
import com.demo.weather.presentation.viewmodel.WeatherInfoViewModel

@Composable
fun WeatherScreen(onNavigateToSearch: () -> Unit) {

    val viewModel: WeatherInfoViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchWeatherInfo()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchView(onSearchClicked = {
            onNavigateToSearch()
        })

        when (uiState) {
            is WeatherScreenState.Loading -> LoadingView()
            is WeatherScreenState.Success -> {
                val weatherInfo = (uiState as WeatherScreenState.Success).weatherInfo
                WeatherInfoScreen(weatherInfo = weatherInfo)
            }

            is WeatherScreenState.Error -> {
                val message = (uiState as WeatherScreenState.Error).message
                ErrorView(message)
            }

            is WeatherScreenState.Empty -> {
                val title = (uiState as WeatherScreenState.Empty).title
                val message = (uiState as WeatherScreenState.Empty).message
                EmptyStateView(title, message)
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error: $message", color = Color.Red)
    }
}

@Composable
fun WeatherInfoScreen(weatherInfo: WeatherInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        WeatherInfo(weatherInfo)
    }
}


@Composable
fun EmptyStateView(title: String, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 30.sp,
            lineHeight = 45.sp,
            fontFamily = FontProvider.poppinsFontFamily,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 15.sp,
            lineHeight = 22.5.sp,
            fontFamily = FontProvider.poppinsFontFamily,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            )
        )
    }
}

@Composable
fun WeatherInfo(weatherInfo: WeatherInfo) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = "https:${weatherInfo.current.condition.icon}",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = weatherInfo.location.name,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location Icon",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${weatherInfo.current.temp_c} \u00B0",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                WeatherDetail(
                    label = stringResource(R.string.humidity),
                    value = "${weatherInfo.current.humidity}%"
                )
                WeatherDetail(
                    label = stringResource(R.string.uv),
                    value = weatherInfo.current.uv.toString()
                )
                WeatherDetail(
                    label = stringResource(R.string.feels_like),
                    value = "${weatherInfo.current.feelslike_c}°"
                )
            }
        }
    }
}

@Composable
fun WeatherDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = FontProvider.poppinsFontFamily,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFC4C4C4)
            )
        )
        Text(
            text = value,
            fontFamily = FontProvider.poppinsFontFamily,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9A9A9A)
            )
        )

    }
}

@Composable
fun SearchView(onSearchClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onSearchClicked() }
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.search_location),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontFamily = FontProvider.poppinsFontFamily
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = Color.Gray
            )
        }
    }
}