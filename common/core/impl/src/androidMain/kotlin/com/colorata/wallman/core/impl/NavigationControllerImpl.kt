package com.colorata.wallman.core.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.colorata.wallman.core.data.Animation
import com.colorata.wallman.core.data.Destination
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.launchMain
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.core.data.module.throwable
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationControllerImpl(private val logger: Logger): NavigationController {

    private var onEvent: (Event) -> Unit = {}

    private fun Destination.normalizedPath() = path.replace("//", "/")
    override fun navigate(destination: Destination) {
        onEvent(Event.Navigate(destination))
    }

    override fun pop() {
        onEvent(Event.Pop)
    }

    override fun resetRootTo(destination: Destination) {
        onEvent(Event.ResetRootTo(destination))
    }

    override val currentPath = MutableStateFlow("")

    @Composable
    override fun NavigationHost(
        startDestination: Destination,
        animation: Animation,
        modifier: Modifier,
        builder: MaterialNavGraphBuilder.() -> Unit
    ) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val scope = rememberCoroutineScope()
        val route = backStackEntry?.destination?.route
        LaunchedEffect(route) {
            currentPath.emit(route.orEmpty())
        }

        LaunchedEffect(Unit) {
            onEvent = { event ->
                scope.launchMain({ logger.throwable(it)}) {
                    when (event) {
                        Event.Pop -> {
                            navController.navigateUp()
                        }

                        is Event.Navigate -> {
                            navController.navigate(
                                event.destination.normalizedPath()
                            )
                        }

                        is Event.ResetRootTo -> {
                            navController.popBackStack()
                            navController.navigate(
                                event.destination.normalizedPath()
                            ) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
        NavHost(navController, startDestination = startDestination.path, modifier = modifier) {
            val navBuilder = MaterialNavGraphBuilder(this, animation)
            navBuilder.builder()
        }
    }

    @Immutable
    private sealed interface Event {
        data object Pop : Event

        data class Navigate(val destination: Destination) : Event

        data class ResetRootTo(val destination: Destination) : Event
    }
}