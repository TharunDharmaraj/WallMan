package com.colorata.wallman.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun animatedDots(dotsCount: Int = 3, delayMillis: Long = 300): String {
    var dots by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            delay(delayMillis)
            if (dots.length == dotsCount) dots = ""
            else dots += "."
        }
    }
    return dots
}

@Composable
fun animateObfuscated(string: String, repeatCount: Int = 4): String {
    var output by remember { mutableStateOf(string) }
    LaunchedEffect(string) {
        repeat(repeatCount) {
            output = output.obfuscated()
            delay(100)
        }
        output = string
    }
    return output
}

private const val symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890~!@#$%^&*()-+="

private fun String.obfuscated(): String {
    return map { symbols.random() }.joinToString("")
}