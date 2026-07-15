package com.orbits.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

/**
 * Bottom navigation destinations.
 */
enum class NavigationItem(val route: String) {
    HOME("home"),
    SEARCH("search")
}

/**
 * Custom Bottom Navigation Bar matching the Instagram dark aesthetic.
 *
 * @param selectedRoute Currently active route.
 * @param onNavigate Callback when a navigation item is selected.
 */
@Composable
fun BottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier,
        containerColor = Color.Black,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == NavigationItem.HOME.route,
            onClick = { onNavigate(NavigationItem.HOME.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedRoute == NavigationItem.SEARCH.route,
            onClick = { onNavigate(NavigationItem.SEARCH.route) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
