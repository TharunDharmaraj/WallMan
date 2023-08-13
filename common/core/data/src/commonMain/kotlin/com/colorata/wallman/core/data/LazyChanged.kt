package com.colorata.wallman.core.data

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LazyChanged<T : Any, V>(
    private val change: () -> V,
    private val value: () -> T
) : ReadOnlyProperty<Any?, T> {
    private var currentChange: V? = null
    private var currentValue: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (change() != currentChange || currentValue == null) {
            currentChange = change()
            currentValue = value()
        }
        return currentValue!!
    }
}

fun <T : Any, V> lazyChanged(change: () -> V, value: () -> T) = LazyChanged(change, value)