package com.colorata.wallman.core.ui.components

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

// TODO: Remove when https://issuetracker.google.com/issues/294242489 fixed
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberModalBottomSheetState(
    skipPartiallyExpanded: Boolean = true,
    animationSpec: FiniteAnimationSpec<Float> = tween()
): SheetState {
    val state = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    LaunchedEffect(Unit) {
        val swipeableStateField = state::class.java.getDeclaredField("swipeableState")
        swipeableStateField.isAccessible = true
        val swipeableState = swipeableStateField.get(state)
        val animationSpecField = swipeableState::class.java.getDeclaredField("animationSpec")
        animationSpecField.isAccessible = true
        animationSpecField.set(swipeableState, animationSpec)
    }
    return state
}