package com.colorata.wallman.core.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable

@Serializable
sealed interface Coordinates {

    @Serializable
    data class ExactCoordinates(val latitude: Float, val longitude: Float): Coordinates

    @Serializable
    data class AddressCoordinates(val address: String): Coordinates
}