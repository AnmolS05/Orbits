package com.orbits.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbits.app.data.local.OrbitDatabase
import com.orbits.app.data.repository.OrbitRepositoryImpl
import com.orbits.app.ui.OrbitViewModel
import com.orbits.app.ui.OrbitViewModelFactory
import com.orbits.app.ui.components.BottomNavBar
import com.orbits.app.ui.components.NavigationItem
import com.orbits.app.ui.screens.HomeScreen
import com.orbits.app.ui.screens.SearchScreen

/**
 * Main Activity of the Orbits application.
 * Hosts the Jetpack Navigation controller and assembles the screens with a pure black style theme.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = OrbitDatabase.getDatabase(applicationContext)
        val dao = database.orbitDao()

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF0095F6),
                    background = Color.Black,
                    surface = Color.Black,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val orbitViewModel: OrbitViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                               val repository = OrbitRepositoryImpl(database.orbitDao())
                               val factory = OrbitViewModelFactory(repository) as T
                               return factory
                            }
                        }
                    )
                    MainAppContent(viewModel = orbitViewModel)
                }
            }
        }
    }
}

/**
 * Assembles bottom navigation bar and displays current screen content.
 *
 * @param viewModel ViewModel state holder.
 */
@Composable
fun MainAppContent(viewModel: OrbitViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationItem.HOME.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.HOME.route) {
                HomeScreen(viewModel = viewModel)
            }
            composable(NavigationItem.SEARCH.route) {
                SearchScreen(viewModel = viewModel)
            }
        }
    }
}
