package com.colorata.wallman.settings.overview.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colorata.animateaslifestyle.material3.isCompact
import com.colorata.animateaslifestyle.shapes.Arc
import com.colorata.animateaslifestyle.shapes.Full
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.continuousComposable
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.core.ui.R
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.components.ArcBorder
import com.colorata.wallman.core.ui.list.VisibilityColumn
import com.colorata.wallman.core.ui.list.VisibilityList
import com.colorata.wallman.core.ui.list.animatedAtLaunch
import com.colorata.wallman.core.ui.list.rememberVisibilityList
import com.colorata.wallman.core.ui.modifiers.detectRotation
import com.colorata.wallman.core.ui.modifiers.displayRotation
import com.colorata.wallman.core.ui.modifiers.rememberRotationState
import com.colorata.wallman.core.ui.shapes.ScallopShape
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.screenPadding
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration
import com.colorata.wallman.settings.about.api.AboutDestination
import com.colorata.wallman.settings.overview.ui.components.AboutItem
import com.colorata.wallman.settings.overview.viewmodel.AboutViewModel
import com.colorata.wallman.ui.icons.BugReport
import com.colorata.wallman.ui.icons.Code
import com.colorata.wallman.ui.icons.CurrencyRuble
import kotlinx.collections.immutable.persistentListOf

context(CoreModule)
fun MaterialNavGraphBuilder.aboutScreen() {
    continuousComposable(Destinations.AboutDestination()) {
        AboutScreen()
    }
}

context(CoreModule)
@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel { AboutViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AboutScreen(state, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreen(state: AboutViewModel.AboutScreenState, modifier: Modifier = Modifier) {

    val animatedItems = rememberVisibilityList { state.aboutItems }.animatedAtLaunch()

    val windowSize = LocalWindowSizeConfiguration.current

    if (windowSize.isCompact()) {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = MaterialTheme.spacing.screenPadding)
                .padding(horizontal = MaterialTheme.spacing.screenPadding)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            LargeTopAppBar(title = {
                Text(text = rememberString(Strings.aboutWallMan))
            })
            Logo(
                Modifier
                    .zIndex(3f)
                    .animateVisibility(
                        visible = animatedItems.visible[0],
                        MaterialTheme.animation.emphasizedVerticalSlide()
                    )
            )
            Actions(animatedItems)
        }
    } else {
        Row(
            modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Logo(
                Modifier
                    .weight(1f)
                    .zIndex(3f)
                    .animateVisibility(
                        animatedItems.visible[0],
                        MaterialTheme.animation.emphasizedVerticalSlide()
                    )
            )
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.screenPadding)
                    .padding(bottom = MaterialTheme.spacing.large)
                    .navigationBarsPadding()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                LargeTopAppBar(title = {
                    Text(text = rememberString(Strings.aboutWallMan))
                })
                Actions(animatedItems)
            }
        }
    }
}

@Composable
private fun Actions(
    animatedItems: VisibilityList<AboutViewModel.AboutItem>,
    modifier: Modifier = Modifier
) {
    VisibilityColumn(
        animatedItems,
        modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        AboutItem(
            it,
            Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
private fun Logo(modifier: Modifier = Modifier) {
    val shape = remember { ScallopShape() }
    val rotationState = rememberRotationState()
    Box(
        modifier
            .detectRotation(rotationState), contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .displayRotation(rotationState)
                .clip(shape)
                .background(Color(0xFF2F3032))
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
        )
        val animatedWidth = animateFloatAsState(
            if (rotationState.isRotationInProgress) 8f else 1f,
            label = ""
        ).value * MaterialTheme.spacing.extraSmall
        ArcBorder(
            Arc.Full,
            BorderStroke(animatedWidth, MaterialTheme.colorScheme.primary),
            Modifier
                .displayRotation(rotationState, layer = 1f)
                .fillMaxSize(0.9f)
                .aspectRatio(1f), shape
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "",
                modifier = Modifier
                    .scale(1.3f)
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    WallManPreviewTheme {
        val state = remember {
            AboutViewModel.AboutScreenState(
                aboutItems = persistentListOf(
                    AboutViewModel.AboutItem(
                        Strings.versionOfWallMan,
                        Strings.actualVersion,
                        Icons.Default.Info
                    ) {},
                    AboutViewModel.AboutItem(
                        Strings.developer,
                        Strings.colorata,
                        Icons.Default.Person
                    ) {},
                    AboutViewModel.AboutItem(
                        Strings.groupInTelegram,
                        Strings.tapToOpen,
                        Icons.Default.Send
                    ) {},
                    AboutViewModel.AboutItem(
                        Strings.gitlab,
                        Strings.tapToOpen,
                        Icons.Default.Code
                    ) {},
                    AboutViewModel.AboutItem(
                        Strings.supportWithQiwi,
                        Strings.tapToOpen,
                        Icons.Default.CurrencyRuble
                    ) {},
                    AboutViewModel.AboutItem(
                        Strings.reportBug,
                        Strings.requiresAccountInGitlab,
                        Icons.Default.BugReport
                    ) {

                    }), clicksOnVersion = 0
            ) { }
        }
        AboutScreen(state = state)
    }
}