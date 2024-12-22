package com.demo.search.presentation.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.demo.core.navigation.NavigationProvider
import com.demo.core.navigation.NavigationRoutes
import com.demo.search.presentation.screen.SearchScreen
import javax.inject.Inject

class SearchNavigationProvider @Inject constructor() : NavigationProvider {
    override fun addNavigation(navController: NavHostController, builder: NavGraphBuilder) {
        builder.composable(NavigationRoutes.SEARCH_ROUTE) {
            SearchScreen(onNavigateToTheWeather = {
                navController.popBackStack()
            })
        }
    }
}