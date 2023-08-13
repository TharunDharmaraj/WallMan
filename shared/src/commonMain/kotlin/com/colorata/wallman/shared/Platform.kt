package com.colorata.wallman.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform