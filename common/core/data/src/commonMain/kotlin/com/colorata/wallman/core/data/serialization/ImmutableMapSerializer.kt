package com.colorata.wallman.core.data.serialization

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializableImmutableMap<K, V> = @Serializable(ImmutableMapSerializer::class) ImmutableMap<K, V>

internal class ImmutableMapSerializer<K, V>(
    private val dataSerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) :
    KSerializer<ImmutableMap<K, V>> {
    private class ImmutableMapDescriptor : SerialDescriptor by serialDescriptor<List<String>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.ImmutableMap"
    }

    override val descriptor: SerialDescriptor = ImmutableMapDescriptor()
    override fun serialize(encoder: Encoder, value: ImmutableMap<K, V>) {
        return MapSerializer(dataSerializer, valueSerializer).serialize(encoder, value.toMap())
    }

    override fun deserialize(decoder: Decoder): ImmutableMap<K, V> {
        return MapSerializer(dataSerializer, valueSerializer).deserialize(decoder).toImmutableMap()
    }
}