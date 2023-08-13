package com.colorata.wallman.core.ui.modifiers

import androidx.compose.ui.Modifier

fun Modifier.runWhen(condition: Boolean, block: Modifier.() -> Modifier) =
    this.then(if (condition) block() else Modifier)