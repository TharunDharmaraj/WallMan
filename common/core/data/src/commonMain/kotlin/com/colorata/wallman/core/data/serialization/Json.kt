package com.colorata.wallman.core.data.serialization

import com.colorata.wallman.core.data.Coordinates
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(ImmutableList::class) { args -> ImmutableListSerializer(args[0]) }
        contextual(ImmutableMap::class) { args ->
            ImmutableMapSerializer(
                args[0],
                args[1]
            )
        }

        polymorphic(Coordinates::class) {
            subclass(Coordinates.ExactCoordinates::class)
            subclass(Coordinates.AddressCoordinates::class)
        }
    }
}