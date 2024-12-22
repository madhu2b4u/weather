package com.demo.search.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.demo.core.ui.font.FontProvider
import com.demo.core.weather.WeatherScreenState
import com.demo.core.weather.model.WeatherInfo
import com.demo.search.R
import com.demo.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(onNavigateToTheWeather: () -> Unit) {
    val viewModel: SearchViewModel = hiltViewModel()
    var searchQuery by remember { mutableStateOf("") }
    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { viewModel.searchCity(searchQuery) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (weatherState) {
            is WeatherScreenState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            is WeatherScreenState.Error -> {
                Text(
                    text = (weatherState as WeatherScreenState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is WeatherScreenState.Success -> {
                val weatherInfo = (weatherState as WeatherScreenState.Success).weatherInfo
                WeatherCard(weatherInfo)
            }

            is WeatherScreenState.Empty -> {

            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF2F2F2))
            .border(16.dp, Color(0xFFF2F2F2), RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                text = stringResource(R.string.search_city),
                fontFamily = FontProvider.poppinsFontFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
        },
        textStyle = TextStyle(
            fontFamily = FontProvider.poppinsFontFamily,
            fontSize = 14.sp,
            color = Color.Black
        ),
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true
    )
}

@Composable
private fun WeatherCard(weather: WeatherInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors((Color(0xFFF2F2F2)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = weather.location.name,
                    fontSize = 20.sp,
                    fontFamily = FontProvider.poppinsFontFamilyBold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${weather.current.temp_c}°",
                    fontFamily = FontProvider.poppinsFontFamilyBold,
                    fontSize = 60.sp,
                )
            }

            AsyncImage(
                model = "https:" + weather.current.condition.icon,
                contentDescription = "Weather Icon",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}