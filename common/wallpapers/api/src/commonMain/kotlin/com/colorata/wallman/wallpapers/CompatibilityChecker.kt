package com.colorata.wallman.wallpapers

fun interface CompatibilityChecker {
    fun isCompatible(): Boolean
}

internal fun trueCompatibilityChecker() = CompatibilityChecker { true }
internal fun falseCompatibilityChecker() = CompatibilityChecker { false }