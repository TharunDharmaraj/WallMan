package com.colorata.wallman.core.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.colorata.animateaslifestyle.Transition
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_BETWEEN_ITEMS = 100L

@Stable
interface VisibilityList<T> : ImmutableList<T> {
    val visible: SnapshotStateList<Boolean>
}

context(CoroutineScope)
suspend fun <T> VisibilityList<T>.animateVisibility(
    visible: Boolean,
    startIndex: Int = 0,
    delayBetweenItems: Long = 50
) {
    suspend fun withAsync(block: suspend () -> Unit) {
        launch {
            block()
        }
    }

    suspend fun animate(index: Int, forward: Boolean = true) {
        if (index in indices) {
            withAsync {
                this.visible[index] = visible
            }
            withAsync {
                delay(delayBetweenItems)
                animate(if (forward) index + 1 else index - 1, forward)
            }
        }
    }
    if (startIndex > 0) {
        withAsync {
            animate(startIndex, false)
        }
        withAsync {
            animate(startIndex)
        }
    } else {
        animate(startIndex)
    }
}

context(CoroutineScope)
suspend fun <T> VisibilityList<T>.animateVisibilityAsGrid(
    visible: Boolean,
    cells: Int,
    startIndex: Int = 0,
    delayBetweenItems: Long = 50
) {
    suspend fun withAsync(block: suspend () -> Unit) {
        launch {
            block()
        }
    }

    val already = map { false }.toMutableList()
    suspend fun animate(index: Int, forward: Boolean = true) {
        if (index in indices && !already[index]) {
            withAsync {
                this.visible[index] = visible
            }
            withAsync {
                val finalIndex = if (forward) index + 1 else index - 1
                if (finalIndex in indices) {
                    delay(delayBetweenItems)
                    animate(finalIndex, forward)
                    already[finalIndex] = true
                }
            }
            withAsync {
                val finalIndex = if (forward) index + cells else index - cells
                if (finalIndex in indices) {
                    delay(delayBetweenItems)
                    animate(finalIndex, forward)
                    already[finalIndex] = true
                }
            }
        }
    }

    if (startIndex > 0) {
        withAsync {
            animate(startIndex, false)
        }
        withAsync {
            animate(startIndex)
        }
    } else {
        animate(startIndex)
    }
}

context(CoroutineScope)
suspend fun <T> VisibilityList<T>.animateVisibility(
    visibleIndexes: List<Int>,
    firstVisibleIndex: Int,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
) {
    indices.forEach { index ->
        visible[index] = index !in visibleIndexes
    }
    animateVisibility(
        visible = true,
        startIndex = if (firstVisibleIndex !in indices) lastIndex else firstVisibleIndex,
        delayBetweenItems = delayBetweenItems
    )
}

context(CoroutineScope)
suspend fun <T> VisibilityList<T>.animateVisibilityAsGrid(
    cells: Int,
    visibleIndexes: List<Int>,
    firstVisibleIndex: Int,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
) {
    indices.forEach { index ->
        visible[index] = index !in visibleIndexes
    }
    animateVisibilityAsGrid(
        visible = true,
        cells = cells,
        startIndex = if (firstVisibleIndex !in indices) lastIndex else firstVisibleIndex,
        delayBetweenItems = delayBetweenItems
    )
}

internal class VisibilityListImpl<T>(
    initial: ImmutableList<T>, visible: Boolean = false
) : VisibilityList<T>, ImmutableList<T> by initial {
    override val visible = initial.map { visible }.toMutableStateList()
}

fun <T> VisibilityList(visible: Boolean, vararg values: T): VisibilityList<T> =
    VisibilityListImpl(values.toList().toImmutableList(), visible)

fun <T> Iterable<T>.toVisibilityList(visible: Boolean): VisibilityList<T> =
    VisibilityListImpl(toImmutableList(), visible)

@Composable
fun <T> rememberVisibilityList(
    visible: Boolean = false,
    vararg keys: Any?,
    builder: @DisallowComposableCalls () -> List<T>
): VisibilityList<T> {
    return remember(*keys) { builder().toVisibilityList(visible) }
}

@Composable
fun <T> rememberVisibilityList(
    count: Int,
    visible: Boolean = false,
    builder: @DisallowComposableCalls (index: Int) -> T
): VisibilityList<T> {
    return remember(count) { List(count) { builder(it) }.toVisibilityList(visible) }
}

@Composable
fun <T> VisibilityList<T>.animatedAtLaunch(
    startIndex: Int = 0,
    delayBetweenItems: Long = 50
): VisibilityList<T> {
    LaunchedEffect(Unit) {
        indices.forEach {
            visible[it] = false
        }
        animateVisibility(
            visible = true,
            startIndex = startIndex,
            delayBetweenItems = delayBetweenItems
        )
    }
    return this
}


@Composable
fun <T> VisibilityList<T>.animatedAtLaunch(
    listState: LazyListState,
    indexOffset: Int = 0,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
): VisibilityList<T> {
    LaunchedEffect(Unit) {
        val visibleIndexes = listState.layoutInfo.visibleItemsInfo.map { it.index - indexOffset }
        val firstVisibleIndex = (listState.firstVisibleItemIndex - indexOffset).coerceAtLeast(0)
        animateVisibility(visibleIndexes, firstVisibleIndex, delayBetweenItems)
    }
    return this
}

@Composable
fun <T> VisibilityList<T>.animatedAsGridAtLaunch(
    cells: Int,
    startIndex: Int = 0,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
): VisibilityList<T> {
    LaunchedEffect(Unit) {
        indices.forEach {
            visible[it] = false
        }
        animateVisibilityAsGrid(
            visible = true,
            cells = cells,
            startIndex = startIndex,
            delayBetweenItems = delayBetweenItems
        )
    }
    return this
}

@Composable
fun <T> VisibilityList<T>.animatedAsGridAtLaunch(
    cells: Int,
    gridState: LazyGridState,
    indexOffset: Int = 0,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
): VisibilityList<T> {
    LaunchedEffect(this) {
        val visibleIndexes = gridState.layoutInfo.visibleItemsInfo.map { it.index - indexOffset }
        val firstVisibleIndex = (gridState.firstVisibleItemIndex - indexOffset).coerceAtLeast(0)
        animateVisibilityAsGrid(
            cells,
            visibleIndexes,
            firstVisibleIndex,
            delayBetweenItems
        )
    }
    return this
}

@Composable
fun <T> VisibilityList<T>.animatedAsGridAtLaunch(
    cells: Int,
    gridState: LazyStaggeredGridState,
    indexOffset: Int = 0,
    delayBetweenItems: Long = DELAY_BETWEEN_ITEMS
): VisibilityList<T> {
    LaunchedEffect(this) {
        val visibleIndexes = gridState.layoutInfo.visibleItemsInfo.map { it.index - indexOffset }
        val firstVisibleIndex = (gridState.firstVisibleItemIndex - indexOffset).coerceAtLeast(0)
        animateVisibilityAsGrid(
            cells,
            visibleIndexes,
            firstVisibleIndex,
            delayBetweenItems
        )
    }
    return this
}


@Composable
fun <T> VisibilityColumn(
    items: VisibilityList<T>,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    transition: @Composable (item: T) -> Transition = { MaterialTheme.animation.emphasizedVerticalSlide() },
    content: @Composable ColumnScope.(item: T) -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        items.forEachIndexed { index, item ->
            Box(Modifier.animateVisibility(items.visible[index], transition(item))) {
                this@Column.content(item)
            }
        }
    }
}

@Composable
fun <T> VisibilityRow(
    items: VisibilityList<T>,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    transition: @Composable (item: T) -> Transition = { MaterialTheme.animation.emphasizedVerticalSlide() },
    content: @Composable RowScope.(item: T, modifier: Modifier) -> Unit
) {
    Row(
        modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        items.forEachIndexed { index, item ->
            content(item, modifier.animateVisibility(items.visible[index], transition(item)))
        }
    }
}