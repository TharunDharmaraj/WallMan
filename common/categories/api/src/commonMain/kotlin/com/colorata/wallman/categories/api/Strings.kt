package com.colorata.wallman.categories.api

import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.simplifiedLocaleOf


val Strings.appulse
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Appulse", "Парад планет"), simplifiedLocaleOf(
            "Explore whole Solar System with Your device.",
            "Исследуйте всю Солнечную систему с Вашим устройством."
        )
    )

val Strings.wonders
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Wonders of the World", "Чудеса света"), simplifiedLocaleOf(
            "Look at Wonders of the World right from Your device.",
            "Смотрите на чудеса света прямо с Вашего устройства."
        )
    )

val Strings.peaceful
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Picturesque places", "Живописные места"), simplifiedLocaleOf(
            "You can look at 3 things endlessly: how fire burns, how water flows and how seagulls fly.",
            "Бесконечно можно смотреть на 3 вещи: как горит огонь, как течёт вода и как летают чайки."
        )
    )

val Strings.fancy
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Flight of fancy", "Полет фантазии"), simplifiedLocaleOf(
            "Look at abstractions in a new way.", "Посмотрите на абстракции по-новому."
        )
    )

val Strings.garden
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Your little garden", "Твой маленький сад"), simplifiedLocaleOf(
            "Take a look! A moth orchid has bloomed in your garden!",
            "Взгляни! В твоем саду расцвела орхидея-мотылек!"
        )
    )

val Strings.birdies
    get() = WallpaperCategoryLocale(
        simplifiedLocaleOf("Birdies", "Птички"),
        simplifiedLocaleOf(
            "Elegant plumage of elegant birds.",
            "Элегантное оперение элегантных птиц."
        )
    )