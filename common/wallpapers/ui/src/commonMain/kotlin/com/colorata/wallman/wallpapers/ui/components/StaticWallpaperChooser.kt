package com.colorata.wallman.wallpapers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.wallpapers.StaticWallpaperApplyVariant
import kotlinx.coroutines.launch

@Composable
fun StaticWallpaperChooser(
    onClick: (StaticWallpaperApplyVariant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        Text(rememberString(Strings.setWallpaperOn), style = MaterialTheme.typography.headlineSmall)
        Column(
            Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.medium,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StaticWallpaperApplyVariant.entries.forEach { variant ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable {
                            onClick(variant)
                        }
                        .heightIn(min = 80.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(MaterialTheme.spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.small,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
                        Icon(
                            variant.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        if (variant.secondIcon != null) {
                            Icon(
                                variant.secondIcon!!,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Text(
                        rememberString(variant.previewName),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun StaticWallpaperChooserPreview() {
    WallManPreviewTheme {
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        var openSheet by remember { mutableStateOf(true) }
        Box(
            Modifier
                .clickable { openSheet = true }
                .fillMaxSize())
        if (openSheet) {
            ModalBottomSheet(
                onDismissRequest = { openSheet = false },
                sheetState = state
            ) {
                StaticWallpaperChooser(
                    onClick = {
                        scope.launch { state.hide() }.invokeOnCompletion {
                            if (!state.isVisible) {
                                openSheet = false
                            }
                        }
                    },
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(MaterialTheme.spacing.extraLarge)
                )
            }
        }
    }
}