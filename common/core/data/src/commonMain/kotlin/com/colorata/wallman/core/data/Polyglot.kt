package com.colorata.wallman.core.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.colorata.wallman.core.data.serialization.SerializableImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import java.util.Locale
import kotlin.reflect.KProperty

interface LocaleCurrent {
    val value: String
}

fun simplifiedLocaleOf(
    english: String, russian: String = english
) = Polyglot(
    english,
    persistentMapOf(
        "en" to english,
        "ru" to russian
    )
)

fun Polyglot.formatted(vararg args: Any?) = simplifiedLocaleOf(
    languageMap["en"]?.format(*args) ?: default,
    languageMap["ru"]?.format(*args) ?: default
)

operator fun Polyglot.plus(other: Polyglot) = simplifiedLocaleOf(
    languageMap["en"] + other.languageMap["en"],
    languageMap["ru"] + other.languageMap["ru"]
)

@Composable
fun rememberString(string: Polyglot, vararg args: Any?) =
    remember(string, args) { string.value }.format(*args)

@Composable
fun rememberString(string: Polyglot) =
    remember(string) { string.value }


operator fun LocaleCurrent.getValue(thisObj: Any?, property: KProperty<*>) = value

@Serializable
data class Polyglot(
    val default: String,
    val languageMap: SerializableImmutableMap<String, String>
) : LocaleCurrent {
    override val value: String
        get() = languageMap[Locale.getDefault().language] ?: default
}