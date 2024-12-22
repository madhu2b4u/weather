package com.demo.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NavigationProvider {
    fun addNavigation(navController: NavHostController, builder: NavGraphBuilder)
}