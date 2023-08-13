package com.colorata.wallman.core.data

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlin.reflect.KProperty


class MaterialNavGraphBuilder(
    private val builder: NavGraphBuilder,
    val animation: Animation
) {
    fun composable(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        deepLinks: List<NavDeepLink> = emptyList(),
        enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
        exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
        popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
        popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
    ) {
        builder.composable(
            route = route,
            arguments = arguments,
            deepLinks = deepLinks,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            content = content
        )
    }

    fun DestinationArgument.toNavArgument() = navArgument(name) {
        type = NavType.StringType
        this.defaultValue = this@toNavArgument.defaultValue
    }
}

@Immutable
interface NavigationDestinationScope {
    operator fun getValue(thisObj: Any?, property: KProperty<Any?>): String?
}

internal class NavigationDestinationScopeImpl(private val entry: NavBackStackEntry) :
    NavigationDestinationScope {
    override operator fun getValue(thisObj: Any?, property: KProperty<Any?>): String? {
        return entry.arguments?.getString(property.name)
    }
}

fun NavigationDestinationScope.parameter(): NavigationDestinationScope = this

fun MaterialNavGraphBuilder.flatComposable(
    destination: Destination,
    hasContinuousChildren: Boolean = false,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        materialFadeThroughIn(initialScale = 1f, durationMillis = animation.durationSpec.medium2)
    },
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (hasContinuousChildren) materialSharedAxisXOut(
            forward = true,
            100,
            animation.durationSpec.medium2
        )
        else materialFadeThroughOut(durationMillis = animation.durationSpec.medium2)
    },
    popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (hasContinuousChildren) materialSharedAxisXIn(
            forward = false,
            100,
            animation.durationSpec.medium2
        )
        else materialFadeThroughIn(initialScale = 1f, durationMillis = animation.durationSpec.medium2)
    },
    popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        materialFadeThroughOut(durationMillis = animation.durationSpec.medium2)
    },
    content: @Composable NavigationDestinationScope.() -> Unit
) {
    composable(
        destination.name,
        arguments = destination.arguments.map { it.toNavArgument() },
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    ) {
        val scope = remember { NavigationDestinationScopeImpl(it) }
        content(scope)
    }
}

fun MaterialNavGraphBuilder.continuousComposable(
    destination: Destination,
    hasContinuousChildren: Boolean = false,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        materialSharedAxisXIn(forward = true, 100, durationMillis = animation.durationSpec.medium2)
    },
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (hasContinuousChildren) materialSharedAxisXOut(
            forward = true,
            100,
            animation.durationSpec.medium2
        )
        else materialFadeThroughOut(durationMillis = animation.durationSpec.medium2)
    },
    popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (hasContinuousChildren) materialSharedAxisXIn(
            forward = false,
            100,
            animation.durationSpec.medium2
        )
        else materialFadeThroughIn(durationMillis = animation.durationSpec.medium2)
    },
    popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        materialSharedAxisXOut(
            forward = false, 100, durationMillis = animation.durationSpec.medium2
        )
    },
    content: @Composable NavigationDestinationScope.() -> Unit
) = flatComposable(
    destination,
    hasContinuousChildren,
    enterTransition,
    exitTransition,
    popEnterTransition,
    popExitTransition,
    content
)