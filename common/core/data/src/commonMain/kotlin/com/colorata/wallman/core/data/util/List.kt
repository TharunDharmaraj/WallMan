package com.colorata.wallman.core.data.util

fun <T> List<T>.takeLastOrLess(n: Int): List<T> = if (n > size) this else takeLast(n)

fun <T> MutableList<T>.addIfNotExists(value: T) {
    if (value !in this) add(value)
}