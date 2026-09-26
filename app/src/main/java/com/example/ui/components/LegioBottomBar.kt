package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun LegioBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            title = "Inicio",
            route = Screen.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        BottomNavItem(
            title = "Usuarios",
            route = Screen.UsersList.route,
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People,
            testTag = "nav_users"
        ),
        BottomNavItem(
            title = "Biblioteca",
            route = Screen.Library.route,
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook,
            testTag = "nav_library"
        ),
        BottomNavItem(
            title = "Eventos",
            route = Screen.Events.route,
            selectedIcon = Icons.Filled.Event,
            unselectedIcon = Icons.Outlined.Event,
            testTag = "nav_events"
        ),
        BottomNavItem(
            title = "Cartelera",
            route = Screen.NewsList.route,
            selectedIcon = Icons.Filled.Feed,
            unselectedIcon = Icons.Outlined.Feed,
            testTag = "nav_news"
        )
    )

    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("legio_bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route ||
                    (item.route == Screen.Events.route && (currentRoute == Screen.EventsList.route || currentRoute == Screen.EventsCalendar.route)) ||
                    (item.route == Screen.Library.route && (currentRoute == Screen.LibraryCategories.route || currentRoute == Screen.LibrarySearch.route))

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LegioWinePrimary,
                    selectedTextColor = LegioWinePrimary,
                    indicatorColor = LegioGoldAccent.copy(alpha = 0.25f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
