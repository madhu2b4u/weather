package com.demo.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.demo.core.navigation.NavigationProvider
import com.demo.core.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationProviders: Set<@JvmSuppressWildcards NavigationProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.WEATHER_ROUTE
            ) {
                navigationProviders.forEach { provider ->
                    provider.addNavigation(navController, this)
                }
            }
        }
    }
}