package com.colorata.wallman.shared

import androidx.compose.runtime.Composable
import com.colorata.wallman.core.data.Destination

@Composable
fun App(startDestination: Destination) {
    Navigation(startDestination)
}