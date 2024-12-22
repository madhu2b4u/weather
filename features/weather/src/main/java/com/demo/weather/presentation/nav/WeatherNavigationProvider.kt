package com.demo.weather.presentation.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.demo.core.navigation.NavigationProvider
import com.demo.core.navigation.NavigationRoutes
import com.demo.weather.presentation.screen.WeatherScreen
import javax.inject.Inject

class WeatherNavigationProvider @Inject constructor() : NavigationProvider {
    override fun addNavigation(navController: NavHostController, builder: NavGraphBuilder) {
        builder.composable(NavigationRoutes.WEATHER_ROUTE) {
            WeatherScreen(onNavigateToSearch = {
                navController.navigate(NavigationRoutes.SEARCH_ROUTE)
            })
        }
    }
}