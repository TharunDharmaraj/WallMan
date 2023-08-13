package com.colorata.wallman.shared

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.colorata.animateaslifestyle.material3.isCompact
import com.colorata.animateaslifestyle.scale
import com.colorata.animateaslifestyle.stagger.toPx
import com.colorata.wallman.categories.ui.categoriesScreen
import com.colorata.wallman.categories.ui.categoryDetailsScreen
import com.colorata.wallman.core.data.Destination
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.destination
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.di.LocalGraph
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.list.VisibilityColumn
import com.colorata.wallman.core.ui.list.VisibilityRow
import com.colorata.wallman.core.ui.list.animatedAtLaunch
import com.colorata.wallman.core.ui.list.rememberVisibilityList
import com.colorata.wallman.core.ui.theme.LocalPaddings
import com.colorata.wallman.core.ui.theme.emphasizedHorizontalSlide
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration
import com.colorata.wallman.greeting.ui.greetingScreen
import com.colorata.wallman.settings.memory.ui.cacheScreen
import com.colorata.wallman.settings.mirror.ui.mirrorScreen
import com.colorata.wallman.settings.overview.ui.aboutScreen
import com.colorata.wallman.settings.overview.ui.settingsScreen
import com.colorata.wallman.wallpapers.ui.mainScreen
import com.colorata.wallman.wallpapers.ui.wallpaperDetailsScreen
import com.colorata.wallman.widget.ui.shapePickerScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Navigation(startDestination: Destination) {
    val navController = LocalGraph.current.coreModule.navigationController
    val windowSize = LocalWindowSizeConfiguration.current
    val route by navController.currentPath.collectAsState()
    val clickOnRoute =
        remember { { newRoute: String -> navController.resetRootTo(destination(newRoute)) } }

    val isCompact = windowSize.isCompact()
    Scaffold(bottomBar = {
        if (isCompact) BottomBar(route) { clickOnRoute(it) }
    }, modifier = Modifier.semantics { testTagsAsResourceId = true }) { padding ->
        CompositionLocalProvider(
            LocalPaddings provides if (isCompact) PaddingValues(
                bottom = padding.calculateBottomPadding()
            ) else PaddingValues(start = 80.dp + MaterialTheme.spacing.large)
        ) {
            // Not using movableContentOf
            // because it causes "node attached multiple times" exception
            // and also ANR in android
            Navigator(startDestination = startDestination)
        }

        Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            if (!isCompact) SideBar(
                currentRoute = route,
                onClick = clickOnRoute
            )
        }
    }
}

@Composable
private fun Navigator(
    modifier: Modifier = Modifier,
    startDestination: Destination
) {
    val graph = LocalGraph.current
    val navController = graph.coreModule.navigationController
    navController.NavigationHost(
        startDestination,
        MaterialTheme.animation,
        modifier
    ) {
        with(graph.coreModule) {
            settingsScreen()
            aboutScreen()
            mirrorScreen()
        }

        with(graph.wallpapersModule) {
            mainScreen()
            wallpaperDetailsScreen()

            categoriesScreen()
            categoryDetailsScreen()

            cacheScreen()

            greetingScreen()
        }

        with(graph.widgetModule) {
            shapePickerScreen()
        }
    }
}
@Composable
fun BottomBar(currentRoute: String, onClick: (route: String) -> Unit) {
    val visibleItems = rememberVisibilityList {
        quickAccessibleDestinations
    }.animatedAtLaunch()

    val height =
        80.dp + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() else 0.dp

    val dp80inPx = 80.dp.toPx()
    val barVisible =
        remember(currentRoute) { currentRoute in quickAccessibleDestinations.map { it.destination.path } }
    Surface(
        tonalElevation = 3.dp, modifier = Modifier.animateVisibility(
            barVisible, MaterialTheme.animation.emphasizedVerticalSlide(dp80inPx)
        )
    ) {
        Column {
            VisibilityRow(
                visibleItems,
                transition = { MaterialTheme.animation.emphasizedVerticalSlide(dp80inPx) }) { it, modifier ->
                val destination = it.destination
                val selected = currentRoute == destination.path
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected && barVisible) onClick(destination.path)
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) it.filledIcon else it.outlinedIcon,
                            contentDescription = ""
                        )
                    },
                    label = { Text(text = rememberString(string = it.previewName)) },
                    alwaysShowLabel = false,
                    modifier = modifier
                        .testTag(destination.path)
                        .height(80.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height - 80.dp)
            )
        }
    }
}

@Composable
private fun SideBar(
    currentRoute: String,
    onClick: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stagger = rememberVisibilityList {
        quickAccessibleDestinations
    }.animatedAtLaunch()

    val dp80inPx = 80.dp.toPx()
    val sidebarVisible =
        remember(currentRoute) { currentRoute in quickAccessibleDestinations.map { it.destination.path } }

    VisibilityColumn(
        stagger,
        modifier
            .animateVisibility(
                sidebarVisible,
                MaterialTheme.animation.emphasizedHorizontalSlide(-dp80inPx) + scale(from = 0.8f)
            )
            .padding(start = MaterialTheme.spacing.large)
            .width(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(vertical = MaterialTheme.spacing.extraLarge),
        transition = { MaterialTheme.animation.emphasizedHorizontalSlide(-dp80inPx) }
    ) {
        val destination = it.destination
        val selected = destination.path == currentRoute
        NavigationRailItem(
            selected = selected,
            onClick = {
                if (!selected && sidebarVisible) onClick(destination.path)
            },
            icon = {
                Icon(
                    imageVector = if (selected) it.filledIcon else it.outlinedIcon,
                    contentDescription = ""
                )
            }, label = null/* { Text(text = rememberString(string = it.value.previewName)) }*/,
            alwaysShowLabel = false
        )
    }
}