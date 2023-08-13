package com.colorata.wallman.core.data

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun String.encodeToBase64(): String = Base64.encode(toByteArray()).replace("/", ".")

@OptIn(ExperimentalEncodingApi::class)
fun String.decodeFromBase64(): String = Base64.decode(replace(".", "/")).decodeToString()