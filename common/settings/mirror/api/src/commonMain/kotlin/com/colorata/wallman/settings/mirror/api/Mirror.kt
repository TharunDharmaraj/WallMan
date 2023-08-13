package com.colorata.wallman.settings.mirror.api

import com.colorata.wallman.core.data.Polyglot

data class Mirror(
    val name: Polyglot, val url: String
)