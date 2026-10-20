package com.example.smokeless.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smokeless.ui.screens.DashboardScreen
import com.example.smokeless.ui.screens.LogScreen
import com.example.smokeless.ui.screens.SosScreen

object Route {
    const val Dashboard = "dashboard"
    const val Sos = "sos"
    const val Log = "log"
}

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun SmokeLessApp(
    viewModel: SmokeLessViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val destinations = remember {
        listOf(
            BottomDestination(Route.Dashboard, "仪表盘", Icons.Default.Home),
            BottomDestination(Route.Sos, "SOS", Icons.Default.FavoriteBorder),
            BottomDestination(Route.Log, "记录", Icons.AutoMirrored.Filled.List),
        )
    }

    val finishSos: () -> Unit = {
        val popped = navController.popBackStack(Route.Dashboard, inclusive = false)
        if (!popped) {
            navController.navigate(Route.Dashboard) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    val openSos: () -> Unit = {
        navController.navigate(Route.Sos) {
            launchSingleTop = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute != Route.Sos) {
                SmokeLessBottomBar(
                    currentRoute = currentRoute,
                    destinations = destinations,
                    onSelect = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = route != Route.Sos
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Dashboard,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Route.Dashboard) {
                DashboardScreen(
                    uiState = uiState,
                    onRecordSmoke = viewModel::recordSmoke,
                    onOpenSos = openSos,
                )
            }
            composable(Route.Sos) {
                SosScreen(onFinished = finishSos)
            }
            composable(Route.Log) {
                LogScreen(
                    events = uiState.events,
                    nowMillis = uiState.nowMillis,
                    onDelete = viewModel::deleteEvent,
                )
            }
        }
    }
}

@Composable
private fun SmokeLessBottomBar(
    currentRoute: String?,
    destinations: List<BottomDestination>,
    onSelect: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 9.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.10f),
            ),
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                destinations.forEach { destination ->
                    val selected = currentRoute == destination.route
                    val itemColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                        },
                        animationSpec = tween(durationMillis = 220),
                        label = "navColor",
                    )
                    val itemBackground by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.11f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                        },
                        animationSpec = tween(durationMillis = 220),
                        label = "navBackground",
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(23.dp))
                            .background(itemBackground)
                            .clickable { onSelect(destination.route) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = itemColor,
                            modifier = Modifier.size(21.dp),
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = itemColor,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}
