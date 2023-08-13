package com.colorata.wallman.widget.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.flatComposable
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.widget.api.ShapePickerDestination
import com.colorata.wallman.widget.api.WidgetModule
import com.colorata.wallman.widget.viewmodel.ShapePickerViewModel

context(WidgetModule)
fun MaterialNavGraphBuilder.shapePickerScreen() {
    flatComposable(Destinations.ShapePickerDestination()) {
        ShapePickerScreen()
    }
}

context(WidgetModule)
@Composable
fun ShapePickerScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel { ShapePickerViewModel() }
    val state by viewModel.state.collectAsState()
    ShapePickerScreen(state, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShapePickerScreen(state: ShapePickerViewModel.ScreenState, modifier: Modifier = Modifier) {
    val selectedItem = remember(state.selectedShape) {
        state.shapes.indexOfFirst { it == state.selectedShape }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            LargeTopAppBar(title = {
                Text(text = rememberString(string = Strings.configureWidget))
            }, colors = TopAppBarDefaults.largeTopAppBarColors())
        }
        itemsIndexed(state.shapes) { index, shape ->
            Column(
                Modifier
                    .clip(RoundedCornerShape(MaterialTheme.spacing.large))
                    .clickable {
                        state.onEvent(ShapePickerViewModel.ScreenEvent.ClickOnShape(shape))
                    }
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = shape.resId),
                        contentDescription = rememberString(
                            string = shape.name
                        ),
                        modifier = Modifier
                            .size(160.dp)
                            .aspectRatio(1f),
                        tint = MaterialTheme.colorScheme.surfaceVariant
                    )
                    if (selectedItem == index) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done",
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(MaterialTheme.spacing.small)
                                .align(
                                    Alignment.BottomEnd
                                ),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(text = rememberString(string = shape.name))
            }
        }
    }
}