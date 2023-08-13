package com.colorata.wallman.wallpapers

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.Coordinates
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.module.IntentHandler
import com.colorata.wallman.core.data.simplifiedLocaleOf
import com.colorata.wallman.ui.icons.Bolt
import com.colorata.wallman.ui.icons.Filter2
import com.colorata.wallman.ui.icons.Filter3
import com.colorata.wallman.ui.icons.Filter4
import com.colorata.wallman.ui.icons.Filter6
import com.colorata.wallman.ui.icons.Image
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class WallpaperI(
    val dynamicWallpapers: ImmutableList<DynamicWallpaper>,
    val staticWallpapers: ImmutableList<StaticWallpaper>,
    val parent: WallpaperPacks,
    val category: WallpaperCategory,
    val author: String
) {
    @Immutable
    sealed class ActionType(val available: Boolean, val label: Polyglot) {
        class Install(available: Boolean) :
            ActionType(available, Strings.ApplyStates.notApplied)

        data object Installing :
            ActionType(available = true, Strings.ApplyStates.applying)

        data object Installed :
            ActionType(available = false, Strings.ApplyStates.applied)

        data object Error :
            ActionType(available = true, Strings.ApplyStates.error)
    }

    enum class SelectedWallpaperType(val label: Polyglot, val icon: ImageVector) {
        Dynamic(label = Strings.dynamic, icon = Icons.Default.Bolt),
        Static(label = Strings.static, icon = Icons.Default.Image)
    }
}

fun WallpaperI.isPerformanceDemanding() =
    if (supportsDynamicWallpapers())
        dynamicWallpapers.any { it.performance == DynamicWallpaper.Performance.Demanding }
    else false

fun IntentHandler.goToLiveWallpaper(wallpaper: DynamicWallpaper) {
    goToLiveWallpaper(
        wallpaper.parent.packageName,
        wallpaper.fullServiceName()
    )
}

@Immutable
data class DynamicWallpaper(
    override val previewName: Polyglot,
    override val shortName: Polyglot = previewName,
    override val description: Polyglot,
    val serviceName: String,
    override val previewRes: String,
    override val parent: WallpaperPacks,
    override val coordinates: Coordinates? = null,
    val compatibilityChecker: CompatibilityChecker = trueCompatibilityChecker(),
    val performance: Performance = Performance.Normal
) : BaseWallpaper {

    enum class DynamicWallpaperCacheState(val label: Polyglot) {
        Cached(label = Strings.install),
        Installed(label = Strings.remove),
        NotCached(label = Strings.download),
        Downloading(label = Strings.cancel)
    }

    enum class Performance {
        Normal,
        Demanding
    }
}

fun DynamicWallpaper.fullServiceName() = parent.packageServiceName + serviceName

@Immutable
data class StaticWallpaper(
    override val previewName: Polyglot,
    override val shortName: Polyglot = previewName,
    override val description: Polyglot,
    val remoteUrl: String,
    val remoteUrlExtension: String = ".png",
    override val previewRes: String,
    override val parent: WallpaperPacks,
    override val coordinates: Coordinates? = null
) : BaseWallpaper

fun StaticWallpaper.fullUrl() = parent.urlPart + "/" + remoteUrl + remoteUrlExtension

@Immutable
interface BaseWallpaper {
    val coordinates: Coordinates?
    val shortName: Polyglot
    val previewName: Polyglot
    val description: Polyglot
    val previewRes: String
    val parent: WallpaperPacks
}

fun BaseWallpaper.canBe(other: BaseWallpaper) =
    previewRes == other.previewRes
            && coordinates == other.coordinates

fun WallpaperI.firstWallpaperSet() =
    if (supportsDynamicWallpapers()) dynamicWallpapers
    else if (staticWallpapers.isNotEmpty()) staticWallpapers
    else error("No wallpaper found fo $this")

fun WallpaperI.firstBaseWallpaper() = firstWallpaperSet().first()

fun WallpaperI.firstPreviewRes() = firstBaseWallpaper().previewRes

fun WallpaperI.maxWallpaperCount() =
    maxOf(dynamicWallpapers.size, staticWallpapers.size)

fun WallpaperI.countIcon() =
    when (maxWallpaperCount()) {
        2 -> Icons.Default.Filter2
        3 -> Icons.Default.Filter3
        4 -> Icons.Default.Filter4
        6 -> Icons.Default.Filter6
        else -> null
    }

fun WallpaperI.baseWallpapers(): List<BaseWallpaper> = dynamicWallpapers + staticWallpapers

fun WallpaperI.supportsDynamicWallpapers() =
    dynamicWallpapers.isNotEmpty() && dynamicWallpapers.all { it.compatibilityChecker.isCompatible() }

fun WallpaperI.shortName() = firstBaseWallpaper().shortName

fun DynamicWallpaper.isSameAs(liveWallpaper: WallpaperProvider.LiveWallpaper) =
    parent.packageName == liveWallpaper.packageName &&
            parent.packageServiceName == liveWallpaper.serviceName


class AllWallpapersDSL {
    val wallpapers: MutableList<WallpaperI> = mutableListOf()

    fun wallpaper(block: WallpaperDSL.() -> Unit) {
        val dsl = WallpaperDSL()
        dsl.block()
        wallpapers.add(dsl.create())
    }
}

class WallpaperDSL {
    private val dynamicWallpapers: MutableList<DynamicWallpaper> = mutableListOf()
    private val staticWallpapers: MutableList<StaticWallpaper> = mutableListOf()
    var author: String = "Google"
    var category: WallpaperCategory = WallpaperCategory.Appulse
    var parent: WallpaperPacks = WallpaperPacks.P
    var previewName: Polyglot = simplifiedLocaleOf("")
    var description: Polyglot = simplifiedLocaleOf("")
    var shortName: Polyglot? = null

    var previewRes: String = "p7a_realr_light_preview"
    var coordinates: Coordinates? = null

    var compatibilityChecker: CompatibilityChecker = trueCompatibilityChecker()
    var performance: DynamicWallpaper.Performance = DynamicWallpaper.Performance.Normal
    fun create(): WallpaperI {
        return WallpaperI(
            dynamicWallpapers.toImmutableList(),
            staticWallpapers.toImmutableList(),
            parent,
            category,
            author
        )
    }

    fun dynamicWallpaper(block: DynamicWallpaperDSL.() -> Unit) {
        val dsl = DynamicWallpaperDSL()
        dsl.block()
        val previewName = dsl.previewName ?: previewName
        val shortName = dsl.shortName ?: shortName ?: previewName
        dynamicWallpapers.add(
            DynamicWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = dsl.description ?: description,
                serviceName = dsl.serviceName,
                previewRes = dsl.previewRes ?: previewRes,
                parent = parent,
                coordinates = dsl.coordinates ?: coordinates,
                compatibilityChecker = AndroidVersionCompatibilityChecker(parent.minSdk),
                performance = dsl.performance ?: performance
            )
        )
    }

    fun staticWallpaper(block: StaticWallpaperDSL.() -> Unit) {
        val dsl = StaticWallpaperDSL()
        dsl.block()
        val previewName = dsl.previewName ?: previewName
        val shortName = dsl.shortName ?: shortName ?: previewName
        staticWallpapers.add(
            StaticWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = dsl.description ?: description,
                previewRes = dsl.previewRes ?: previewRes,
                coordinates = dsl.coordinates ?: coordinates,
                parent = parent,
                remoteUrl = dsl.remoteUrl
            )
        )
    }
}

var WallpaperDSL.minSdk: Int
    get() = Build.VERSION_CODES.BASE
    set(value) {
        compatibilityChecker = AndroidVersionCompatibilityChecker(value)
    }

class DynamicWallpaperDSL {
    var shortName: Polyglot? = null
    var previewName: Polyglot? = null
    var description: Polyglot? = null
    var serviceName: String = ""

    var previewRes: String? = null
    var coordinates: Coordinates? = null
    var performance: DynamicWallpaper.Performance? = null
}

class StaticWallpaperDSL {
    var shortName: Polyglot? = null
    var previewName: Polyglot? = null
    var description: Polyglot? = null
    var remoteUrl: String = ""

    var previewRes: String? = null
    var coordinates: Coordinates? = null
}

fun createWallpapers(block: AllWallpapersDSL.() -> Unit): Lazy<ImmutableList<WallpaperI>> {
    return lazy {
        val dsl = AllWallpapersDSL()
        dsl.block()
        dsl.wallpapers.toImmutableList()
    }
}

val walls by createWallpapers {
    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Arches National Park", "Нац. парк Арки")
        previewName =
            simplifiedLocaleOf("Arches National Park, Moab, Utah", "Нац. парк Арки, Моаб, штат Юта")
        description = simplifiedLocaleOf(
            "In addition to a variety of geological formations, the park contains over 2,000 natural sandstone arches, including the world-famous Delicate Arch.",
            "Наряду с другими чудесами природы в парке находится более 2000 нерукотворных арок из песчаника. В том числе – всемирно известная Изящная арка."
        )
        previewRes = "p1_moab_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 38.73175f,
            longitude = -109.72572f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.MoabWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_moab_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Garden", "Сад")
        previewName = simplifiedLocaleOf("Garden, Kent, United Kingdom", "Сад, Кент, Великобритани")
        description = simplifiedLocaleOf(
            "With its abundance of fruit orchards and hop gardens, Kent is known as The Garden of England.",
            "Из-за многочисленных фруктовых садов и плантаций хмеля Кент называют \"садом Англии\"."
        )
        previewRes = "p1_kent_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 51.283894f,
            longitude = 0.7860345f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.KentWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_kent_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Half Dome", "Скала Хаф-Доум")
        previewName = simplifiedLocaleOf(
            "Half Dome, Yosemite National Park, CA",
            "Скала Хаф-Доум в Йосемитском нац. парке, штат Калифорния"
        )
        description = simplifiedLocaleOf(
            "This granite formation in Yosemite Valley, named for its distinct shape, rises more than 4,737 ft above the valley floor.",
            "Название этой гранитной скалы переводится как \"полукупол\". Она возвышается над долиной Йосемити на 1450 метров."
        )
        previewRes = "p1_half_dome_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 37.746494f,
            longitude = -119.53273f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.HalfDomeWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_half_dome_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Horizon", "Горизонт")
        previewName = simplifiedLocaleOf("Horizon", "Горизонт")
        description = simplifiedLocaleOf(
            "Look to the horizon. The sun rises as your phone charges, and lowers as the battery is used.",
            "Солнце поднимается над горизонтом, пока ваш телефон заряжается, и опускается, когда расходуется заряд батареи."
        )
        previewRes = "p1_sunset_preview"

        dynamicWallpaper {
            serviceName = ".wallpapers.SunsetOnBatteryWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_sunset_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Wonders

        shortName = simplifiedLocaleOf("Pantheon", "Пантеон")
        previewName = simplifiedLocaleOf("Pantheon, Rome, Italy", "Пантеон, Рим, Италия")
        description = simplifiedLocaleOf(
            "Almost two thousand years after the Pantheon was built, the circular building remains the world's largest unreinforced concrete dome.",
            "Пантеон построен почти 2000 лет назад, но и сегодня его неармированный бетонный купол остается самым большим в мире."
        )
        previewRes = "p1_pantheon_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 41.898643f,
            longitude = 12.476896f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.PantheonWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_pantheon_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Wonders

        shortName = simplifiedLocaleOf("Shijuku", "Район Синдзюку")
        previewName = simplifiedLocaleOf("Shijuku, Tokyo, Japan", "Район Синдзюку, Токио, Япония")
        description = simplifiedLocaleOf(
            "At 656 ft high, this building in Shinjuku, designed by Yoshikazu Uchida, is the 28th tallest building in Tokyo and the 33rd tallest in Japan.",
            "Это здание в районе Синдзюку высотой 200 метров создал архитектор Йошикацу Йошида. Сегодня оно на 28 месте среди самых высоких зданий Токио и на 33 месте – среди небоскребов Японии."
        )
        previewRes = "p1_togo_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 35.691753f,
            longitude = 139.69547f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.TogoWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_togo_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Whitehaven Beach", "Пляж Уайтхэвен")
        previewName = simplifiedLocaleOf(
            "Whitehaven Beach, Queensland, Australia",
            "Пляж Уайтхэвен, Квинсленд, Австралия"
        )
        description = simplifiedLocaleOf(
            "Known for its white sands, Whitehaven Beach is made up of 98% pure silica, which gives it a bright white color.",
            "Пляж Уайтхэвен славится своим белоснежным песком, который на 98% состоит из чистого диоксида кремния."
        )
        previewRes = "p1_whitehaven_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 54.54983f,
            longitude = -3.588655f
        )

        dynamicWallpaper {
            serviceName = ".wallpapers.WhitehavenWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_whitehaven_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P1
        category = WallpaperCategory.Appulse

        shortName = simplifiedLocaleOf("Your world", "Солнечная система")
        previewName = simplifiedLocaleOf("Your world, Solar system", "Солнечная система")
        description = simplifiedLocaleOf(
            "Inspired by Apollo 17's Blue Marble, view Earth from space based on your location, with real-time clouds.",
            "Обои по мотивам знаменитой фотографии, сделанной экипажем корабля \"Аполлон-17\". Вид на Землю из космоса меняется в зависимости от вашего местоположения. Любуйтесь изображением облаков в реальном времени!"
        )
        previewRes = "p1_earth_preview"

        dynamicWallpaper {
            serviceName = ".wallpapers.EarthWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p1_earth_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Gooey", "Капли на стекле")
        description = simplifiedLocaleOf(
            "Touch these adaptable screens to reshape the gooey patterns.",
            "Проведите по экрану или коснитесь его, и изображение изменится."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Marshmallow", "Капли на стекле: маршмеллоу")
            previewRes = "p2_canyons_v1_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Licorice", "Капли на стекле: темная ночь")
            previewRes = "p2_canyons_v2_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Mint", "Капли на стекле: нежная мята")
            previewRes = "p2_canyons_v3_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV3"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Orange crush", "Капли на стекле: солнечный оранжад")
            previewRes = "p2_canyons_v4_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV4"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Vanilla taffy", "Капли на стекле: брызги шампанского")
            previewRes = "p2_canyons_v5_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV5"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Violet dream", "Капли на стекле: северное сияние")
            previewRes = "p2_canyons_v6_preview"
            serviceName = ".canyons.variations.CanyonsWallpaperV6"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Marshmallow", "Капли на стекле: маршмеллоу")
            previewRes = "p2_canyons_v1_preview"
            remoteUrl = "p2_canyons_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Licorice", "Капли на стекле: темная ночь")
            previewRes = "p2_canyons_v2_preview"
            remoteUrl = "p2_canyons_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Gooey, Mint", "Капли на стекле: нежная мята")
            previewRes = "p2_canyons_v3_preview"
            remoteUrl = "p2_canyons_v3_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Orange crush", "Капли на стекле: солнечный оранжад")
            previewRes = "p2_canyons_v4_preview"
            remoteUrl = "p2_canyons_v4_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Vanilla taffy", "Капли на стекле: брызги шампанского")
            previewRes = "p2_canyons_v5_preview"
            remoteUrl = "p2_canyons_v5_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Gooey, Violet dream", "Капли на стекле: северное сияние")
            previewRes = "p2_canyons_v6_preview"
            remoteUrl = "p2_canyons_v6_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Appulse

        previewName = simplifiedLocaleOf("World comes to you", "На орбите")
        description = simplifiedLocaleOf(
            "Journey around the world without packing a suitcase. Your vantage point changes throughout the day.",
            "Чтобы отправиться в кругосветное путешествие, не обязательно паковать чемоданы. Открывайте новые места с течением дня."
        )
        previewRes = "p2_earth_orbit_preview"

        dynamicWallpaper {
            serviceName = ".celestialbodies.EarthOrbitWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_earth_orbit_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("A walk in the clouds", "Атмосферные явления")
        previewName =
            simplifiedLocaleOf("A walk in the clouds, Hawaii", "Атмосферные явления, Гавайи")
        description = simplifiedLocaleOf(
            "Float by the clouds and frequent rain showers that earned Hawaii the nickname “rainbow state.” Kāneʻohe, Hawaii, United States",
            "Проплывите над островами вместе с облаками и проливными дождями, из-за которых Гавайи получили прозвище \"Радужный штат\". Канеохе, Гавайи, США"
        )
        previewRes = "p2_honolulu_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 21.403193f,
            longitude = -157.80019f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.honolulu.HonoluluWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_honolulu_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Glimmer", "Цветоформы")
        description = simplifiedLocaleOf(
            "Catch a glimmer of color as you move or touch your phone.",
            "Коснитесь экрана или наклоните устройство, и изображение изменится."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Candy", "Цветоформы: леденцовое ассорти")
            previewRes = "p2_imprint_v1_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Cream", "Цветоформы: кремовая фантазия")
            previewRes = "p2_imprint_v2_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV2"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Glimmer, Dazzling blue", "Цветоформы: сапфировое мерцание")
            previewRes = "p2_imprint_v3_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV3"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Glimmer, Early morning", "Цветоформы: рассветные мотивы")
            previewRes = "p2_imprint_v4_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV4"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Purple haze", "Цветоформы: фиолетовая дымка")
            previewRes = "p2_imprint_v5_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV5"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Midnight blue", "Цветоформы: синяя вечность")
            previewRes = "p2_imprint_v6_preview"
            serviceName = ".imprint.variations.ImprintWallpaperV6"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Candy", "Цветоформы: леденцовое ассорти")
            previewRes = "p2_imprint_v1_preview"
            remoteUrl = "p2_imprint_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Cream", "Цветоформы: кремовая фантазия")
            previewRes = "p2_imprint_v2_preview"
            remoteUrl = "p2_imprint_v2_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Glimmer, Dazzling blue", "Цветоформы: сапфировое мерцание")
            previewRes = "p2_imprint_v3_preview"
            remoteUrl = "p2_imprint_v3_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Glimmer, Early morning", "Цветоформы: рассветные мотивы")
            previewRes = "p2_imprint_v4_preview"
            remoteUrl = "p2_imprint_v4_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Purple haze", "Цветоформы: фиолетовая дымка")
            previewRes = "p2_imprint_v5_preview"
            remoteUrl = "p2_imprint_v5_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Glimmer, Midnight blue", "Цветоформы: синяя вечность")
            previewRes = "p2_imprint_v6_preview"
            remoteUrl = "p2_imprint_v6_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Your whirled", "В погоне за ветром")
        description = simplifiedLocaleOf(
            "Local wind patterns swirl and twirl in real time, based on your location.",
            "Движение потоков ветра в режиме реального времени (c учетом вашего местоположения)."
        )

        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Blue", "В погоне за ветром: небесная лазурь")
            previewRes = "p2_windy1"
            serviceName = ".windy.variations.Windy1WallpaperService"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Blush", "В погоне за ветром: закатные краски")
            previewRes = "p2_windy2"
            serviceName = ".windy.variations.Windy2WallpaperService"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Midnight", "В погоне за ветром: полночное небо")
            previewRes = "p2_windy3"
            serviceName = ".windy.variations.Windy3WallpaperService"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Your whirled, Sunset", "В погоне за ветром: закат")
            previewRes = "p2_windy4"
            serviceName = ".windy.variations.Windy4WallpaperService"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Turquoise", "В погоне за ветром: бирюзовый бриз")
            previewRes = "p2_windy5"
            serviceName = ".windy.variations.Windy5WallpaperService"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Sky blue", "В погоне за ветром: синяя даль")
            previewRes = "p2_windy6"
            serviceName = ".windy.variations.Windy6WallpaperService"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Blue", "В погоне за ветром: небесная лазурь")
            previewRes = "p2_windy1"
            remoteUrl = "p2_windy1"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Blush", "В погоне за ветром: закатные краски")
            previewRes = "p2_windy2"
            remoteUrl = "p2_windy2"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Midnight", "В погоне за ветром: полночное небо")
            previewRes = "p2_windy3"
            remoteUrl = "p2_windy3"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Your whirled, Sunset", "В погоне за ветром: закат")
            previewRes = "p2_windy4"
            remoteUrl = "p2_windy4"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Turquoise", "В погоне за ветром: бирюзовый бриз")
            previewRes = "p2_windy5"
            remoteUrl = "p2_windy5"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("Your whirled, Sky blue", "В погоне за ветром: синяя даль")
            previewRes = "p2_windy6"
            remoteUrl = "p2_windy6"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Calming coastline", "Безмятежный прибой")
        previewName = simplifiedLocaleOf("Calming coastline, Lagos", "Безмятежный прибой, Лагуш")
        description = simplifiedLocaleOf(
            "Watch live turquoise waves lap against Ponta da Piedade, a group of rock formations along the Atlantic coast. Lagos, Portugal",
            "Посмотрите, как бирюзовые волны Атлантического океана накатывают на скалы Понта-да-Пьедаде. Лагуш, Португалия."
        )
        previewRes = "p2_lagos_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 37.118816f,
            longitude = -8.685016f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.lagos.LagosWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_lagos_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Appulse

        previewName = simplifiedLocaleOf("Planet red", "Красная планета")
        description = simplifiedLocaleOf(
            "Glimpse real-time sunrise and sunset over Mars’ ice caps while thin, wispy clouds float by the Valles Marineris canyon.",
            "Вы увидите настоящие закаты и рассветы над ледяными шапками Марса, а также невесомые облака, проплывающие над долинами Маринер."
        )
        previewRes = "p2_mars_preview"

        dynamicWallpaper {
            serviceName = ".celestialbodies.MarsWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_mars_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Flying above", "Взгляд с высоты")
        previewName = simplifiedLocaleOf(
            "Flying above, Monument Valley",
            "Взгляд с высоты, Долина монументов"
        )
        description = simplifiedLocaleOf(
            "Hot-air balloons float by Monument Valley’s red sandstone formations. Monument Valley, Utah, United States",
            "Воздушные шары пролетают над скалами из красного песчаника. Долина монументов, Юта, США."
        )
        previewRes = "p2_monument_valley_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 37.14054f,
            longitude = -110.20773f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.monumentvalley.MonumentValleyWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_monument_valley_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Hint of salt", "Вода и соль")
        previewName =
            simplifiedLocaleOf("Hint of salt, Salar de Uyuni", "Вода и соль, солончак Уюни")
        description = simplifiedLocaleOf(
            "See the world’s largest salt flat, Salar de Uyuni, a breeding ground for several species of flamingos. Salar de Uyuni, Bolivia",
            "Самый большой в мире солончак, на котором обзаводятся потомством несколько видов фламинго. Солончак Уюни, Боливия."
        )
        previewRes = "p2_uyuni_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = -20.116491f,
            longitude = -67.48218f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.uyuni.UyuniWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_uyuni_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P2
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Hidden fires", "Внутренний огонь")
        previewName =
            simplifiedLocaleOf("Hidden fires, Mount Vesuvius", "Внутренний огонь, вулкан Везувий")
        description = simplifiedLocaleOf(
            "Birds soar above Mount Vesuvius volcano, which destroyed Pompeii in AD 79 and erupted in 1944. Mount Vesuvius, Campania, Italy",
            "Полет птиц над Везувием – вулканом, который уничтожил Помпеи в 79 г. н. э. Последнее извержение произошло в 1944 г. Вулкан Везувий, Кампания, Италия."
        )
        previewRes = "p2_vesuvius_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 40.823227f,
            longitude = 14.428777f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.vesuvius.VesuviusWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p2_vesuvius_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Appulse

        previewName = simplifiedLocaleOf("Moon shot", "На обратной стороне")
        description = simplifiedLocaleOf(
            "Get closer to the moon and observe its real-time lunar phases.",
            "Следите за сменой фаз луны в реальном времени."
        )
        previewRes = "p3_moon_preview"

        dynamicWallpaper {
            serviceName = ".celestialBodies.wallpapers.variations.MoonWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p3_moon_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("White Sands", "Белый песок")
        previewName =
            simplifiedLocaleOf("White Sands, Sonoran Desert", "Белый песок: пустыня Сонора")
        description = simplifiedLocaleOf(
            "Watch wind carry dancing sand across the Sonoran Desert, spanning the United States and Mexico. Sonoran Desert, Mexico",
            "Песок пляшет на крыльях ветра в пустыне, раскинувшейся на территории США и Мексики. Пустыня Сонора, Мексика."
        )
        previewRes = "p3_mexico_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 32.24909f,
            longitude = -112.9164f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.mexico.wallpapers.MexicoWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p3_mexico_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Groove", "В такт музыке")
        description = simplifiedLocaleOf(
            "Pixel moves to the music...your music. When music is playing, you’ll see colors shift and sway to the rhythm.",
            "Слушайте музыку и смотрите, как цветные волны перемещаются по экрану и танцуют в такт."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Groove, Chill", "В такт музыке: спокойствие")
            previewRes = "p3_sound_viz_v1_preview"
            serviceName = ".soundviz.wallpaper.variations.SoundVizWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Groove, Party", "В такт музыке: веселье")
            previewRes = "p3_sound_viz_v2_preview"
            serviceName = ".soundviz.wallpaper.variations.SoundVizWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Groove, After Dark", "В такт музыке: сумерки")
            previewRes = "p3_sound_viz_v3_preview"
            serviceName = ".soundviz.wallpaper.variations.SoundVizWallpaperV4"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Groove, Chill", "В такт музыке: спокойствие")
            previewRes = "p3_sound_viz_v1_preview"
            remoteUrl = "p3_sound_viz_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Groove, Party", "В такт музыке: веселье")
            previewRes = "p3_sound_viz_v2_preview"
            remoteUrl = "p3_sound_viz_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Groove, After Dark", "В такт музыке: сумерки")
            previewRes = "p3_sound_viz_v3_preview"
            remoteUrl = "p3_sound_viz_v3_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Wonders

        shortName = simplifiedLocaleOf("City Silhouette", "Городской пейзаж")
        description = simplifiedLocaleOf(
            "Watch the City that Never Sleeps change dynamically throughout the day and night. New York, United States",
            "Город, который никогда не спит. Наблюдайте за сменой дня и ночи в Нью-Йорке."
        )

        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("City Silhouette, New York", "Городской пейзаж: Нью-Йорк")
            previewRes = "p3_cities_v2_preview"
            serviceName = ".cities.wallpapers.variations.NewYorkWallpaper"
            coordinates = Coordinates.ExactCoordinates(
                latitude = 40.71302f,
                longitude = -73.99754f
            )
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf(
                "City Silhouette, San Francisco",
                "Городской пейзаж: Сан-Франциско"
            )
            previewRes = "p3_cities_v1_preview"
            serviceName = ".cities.wallpapers.variations.SanFranciscoWallpaper"
            coordinates = Coordinates.ExactCoordinates(
                latitude = 37.773438f,
                longitude = -122.46176f
            )
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("City Silhouette, New York", "Городской пейзаж: Нью-Йорк")
            previewRes = "p3_cities_v2_preview"
            remoteUrl = "p3_cities_v2_preview"
            coordinates = Coordinates.ExactCoordinates(
                latitude = 40.71302f,
                longitude = -73.99754f
            )
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf(
                "City Silhouette, San Francisco",
                "Городской пейзаж: Сан-Франциско"
            )
            previewRes = "p3_cities_v1_preview"
            remoteUrl = "p3_cities_v1_preview"
            coordinates = Coordinates.ExactCoordinates(
                latitude = 37.773438f,
                longitude = -122.46176f
            )
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("A Drop in the Ocean", "Капля в море")
        description = simplifiedLocaleOf(
            "Feel the current of the world around you, with bodies of water and terrain taking on a watercolor form.",
            "Мир вокруг перетекает в абстрактный акварельный пейзаж."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("A Drop in the Ocean, Coral", "Капля в море: коралл")
            previewRes = "p3_surf_and_turf_v1_preview"
            serviceName = ".surfandturf.wallpapers.variations.SurfAndTurfWallpaperV1"
        }
        dynamicWallpaper {
            previewName =
                simplifiedLocaleOf("A Drop in the Ocean, Seaweed", "Капля в море: водоросли")
            previewRes = "p3_surf_and_turf_v2_preview"
            serviceName = ".surfandturf.wallpapers.variations.SurfAndTurfWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("A Drop in the Ocean, Waves", "Капля в море: волны")
            previewRes = "p3_surf_and_turf_v3_preview"
            serviceName = ".surfandturf.wallpapers.variations.SurfAndTurfWallpaperV3"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("A Drop in the Ocean, Coral", "Капля в море: коралл")
            previewRes = "p3_surf_and_turf_v1_preview"
            remoteUrl = "p3_surf_and_turf_v1_preview"
        }
        staticWallpaper {
            previewName =
                simplifiedLocaleOf("A Drop in the Ocean, Seaweed", "Капля в море: водоросли")
            previewRes = "p3_surf_and_turf_v2_preview"
            remoteUrl = "p3_surf_and_turf_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("A Drop in the Ocean, Waves", "Капля в море: волны")
            previewRes = "p3_surf_and_turf_v3_preview"
            remoteUrl = "p3_surf_and_turf_v3_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Appulse

        previewName = simplifiedLocaleOf("New Horizons", "Новые горизонты")
        description = simplifiedLocaleOf(
            "Pluto shows its two faces. Enjoy rich red tones during the day, and a mysterious blue brilliance at night.",
            "Два лица Плутона: красные тона днем и загадочный голубой блеск ночью."
        )
        previewRes = "p3_pluto_preview"

        dynamicWallpaper {
            serviceName = ".celestialBodies.wallpapers.variations.PlutoWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p3_pluto_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Fancy
        author = "Laura Dumitru"

        shortName = simplifiedLocaleOf("Pixie", "Пикси")
        description = simplifiedLocaleOf(
            "Your playful guide awaits. Pixie magically appears when your phone is at rest and goes on a journey when you unlock your phone.",
            "Ваш забавный компаньон ждет вас. Он появляется из ниоткуда, когда ваш телефон отдыхает. Разблокируйте экран, и Пикси исчезнет."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Cobalt", "Пикси: кобальтовый")
            previewRes = "p3_miniman_viz_v1_preview"
            serviceName = ".miniman.wallpaper.variations.MinimanWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Periwinkle", "Пикси: фиолетово-голубой")
            previewRes = "p3_miniman_viz_v2_preview"
            serviceName = ".miniman.wallpaper.variations.MinimanWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Emerald", "Пикси: изумрудный")
            previewRes = "p3_miniman_viz_v3_preview"
            serviceName = ".miniman.wallpaper.variations.MinimanWallpaperV3"
        }

        staticWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Cobalt", "Пикси: кобальтовый")
            previewRes = "p3_miniman_viz_v1_preview"
            remoteUrl = "p3_miniman_viz_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Periwinkle", "Пикси: фиолетово-голубой")
            previewRes = "p3_miniman_viz_v2_preview"
            remoteUrl = "p3_miniman_viz_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Pixie, Emerald", "Пикси: изумрудный")
            previewRes = "p3_miniman_viz_v3_preview"
            remoteUrl = "p3_miniman_viz_v3_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Bird’s-Eye View", "С высоты птичьего полета")
        previewName = simplifiedLocaleOf(
            "Bird’s-Eye View, Zion National Park",
            "С высоты птичьего полета: Национальный парк Зайон"
        )
        description = simplifiedLocaleOf(
            "Take a glimpse of Zion National Park’s vermillion cliffs, reaching up to 2,200 feet. Zion National Park, Utah, United States",
            "Взгляните на ярко-красные скалы, достигающие высоты 700 метров. Национальный парк Зайон в штате Юта, США."
        )
        previewRes = "p3_utah_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 37.426994f,
            longitude = -113.02968f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.utah.wallpapers.UtahWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p3_utah_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Bloom", "Цветение")
        description = simplifiedLocaleOf(
            "Feel the burst when you touch your screen. Your wallpaper also responds to notifications, unlocking, and more.",
            "Коснитесь экрана, и вы увидите салют. Обои также реагируют на уведомления, разблокировку экрана и многое другое."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Sunrise", "Цветение: восход")
            previewRes = "p3_tactile_v2_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV3"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Cloud", "Цветение: облако")
            previewRes = "p3_tactile_v1_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Sky", "Цветение: небо")
            previewRes = "p3_tactile_v3_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV5"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Burst, Forest", "Салют: лес")
            previewRes = "p3_tactile_v4_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV9"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Burst, Midnight", "Салют: полночь")
            previewRes = "p3_tactile_v5_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV11"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Burst, Dusk", "Салют: закат")
            previewRes = "p3_tactile_v6_preview"
            serviceName = ".tactile.wallpapers.TactileWallpaperV12"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Sunrise", "Цветение: восход")
            previewRes = "p3_tactile_v2_preview"
            remoteUrl = "p3_tactile_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Cloud", "Цветение: облако")
            previewRes = "p3_tactile_v1_preview"
            remoteUrl = "p3_tactile_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bloom, Sky", "Цветение: небо")
            previewRes = "p3_tactile_v3_preview"
            remoteUrl = "p3_tactile_v3_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Burst, Forest", "Салют: лес")
            previewRes = "p3_tactile_v4_preview"
            remoteUrl = "p3_tactile_v4_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Burst, Midnight", "Салют: полночь")
            previewRes = "p3_tactile_v5_preview"
            remoteUrl = "p3_tactile_v5_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Burst, Dusk", "Салют: закат")
            previewRes = "p3_tactile_v6_preview"
            remoteUrl = "p3_tactile_v6_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Light the Way", "Свет в пути")
        previewName = simplifiedLocaleOf("Light the Way, Schwyz", "Свет в пути: Швиц")
        description = simplifiedLocaleOf(
            "Escape to the small town of Schwyz, surrounded by the Alps, where the houses come alive at night. Schwyz, Switzerland",
            "Укройтесь от посторонних глаз в альпийском городке Швиц, где дома оживают с наступлением ночи. Швиц, Швейцария."
        )
        previewRes = "p3_switzerland_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 47.062717f,
            longitude = 8.650598f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.switzerland.wallpapers.SwitzerlandWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p3_switzerland_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Tropical Voyage", "Тропическое путешествие")
        previewName = simplifiedLocaleOf(
            "Tropical Voyage, Malolo Barrier Reef",
            "Тропическое путешествие: барьерный риф Малоло"
        )
        description = simplifiedLocaleOf(
            "Dive into the Malolo Barrier Reef, known for its world-class snorkeling, as boats cruise on by. Malolo Barrier Reef, Fiji",
            "Полюбуйтесь барьерным рифом Малоло, одним из лучших мест для сноркелинга, и проплывающими мимо корабликами. Барьерный риф Малоло, Фиджи."
        )
        previewRes = "p3_fiji_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = -17.792616f,
            longitude = 177.11627f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.fiji.wallpapers.FijiWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p3_fiji_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P3
        category = WallpaperCategory.Fancy

        shortName = simplifiedLocaleOf("Bubble Up", "Цветные пузыри")
        description = simplifiedLocaleOf(
            "Colors bounce around the screen as you touch or tilt your phone. Keep an eye out for extra moments of bubbly delight.",
            "Коснитесь экрана или покачайте телефон – цветные пузыри начнут перемещаться по экрану и радовать вас."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Peach", "Цветные пузыри: персик")
            previewRes = "p3_delight_v1_preview"
            serviceName = ".delight.wallpapers.DelightWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Lemon", "Цветные пузыри: лимон")
            previewRes = "p3_delight_v2_preview"
            serviceName = ".delight.wallpapers.DelightWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Plum", "Цветные пузыри: слива")
            previewRes = "p3_delight_v3_preview"
            serviceName = ".delight.wallpapers.DelightWallpaperV4"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Peach", "Цветные пузыри: персик")
            previewRes = "p3_delight_v1_preview"
            remoteUrl = "p3_delight_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Lemon", "Цветные пузыри: лимон")
            previewRes = "p3_delight_v2_preview"
            remoteUrl = "p3_delight_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Bubble Up, Plum", "Цветные пузыри: слива")
            previewRes = "p3_delight_v3_preview"
            remoteUrl = "p3_delight_v3_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Tarout Bay", "Тарут")
        previewName = simplifiedLocaleOf("Tarout Bay, Saudi Arabia", "Тарут (Саудовская Аравия)")
        description = simplifiedLocaleOf(
            "Fluffy clouds slowly wash over Tarout Bay, one of the most ancient sites of human civilization in the Arabian Peninsula.",
            "Пушистые облака медленно плывут над островом Тарут – одним из древнейших обитаемых мест на Аравийском полуострове."
        )
        previewRes = "p4_arabia_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 26.595383f,
            longitude = 50.124588f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.arabia.wallpapers.ArabiaWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p4_arabia_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Compass", "Компас")
        description = simplifiedLocaleOf(
            "Pick a location, and let the compass show you the way.",
            "Выберите направление, и пусть компас укажет вам путь."
        )
        previewRes = "p4_compass_preview"

        dynamicWallpaper {
            serviceName = ".compass.wallpaper.variations.CompassWallpaperV1"
        }
        staticWallpaper {
            remoteUrl = "p4_compass_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Doodle", "Рисунок")
        description = simplifiedLocaleOf(
            "Tap, drag, fling! This wallpaper is yours to create. Tap customize to explore and start fresh.",
            "Водите пальцами по экрану, нажимайте и перетаскивайте! Создайте свои собственные обои. Чтобы начать, нажмите \"Настроить\"."
        )
        previewRes = "p4_doodle_preview"

        dynamicWallpaper {
            serviceName = ".doodle.wallpaper.variations.DoodleWallpaperV1"
        }
        staticWallpaper {
            remoteUrl = "p4_doodle_v1_black"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Fancy
        author = "Anna Kövecses"

        shortName = simplifiedLocaleOf("Garden", "Сад")
        description = simplifiedLocaleOf(
            "Take a moment to rest and enjoy these rustling leaves. Artwork in collaboration with Anna Kövecses.",
            "Отложите дела и насладитесь отдыхом под шум листвы. Создано в сотрудничестве с Анной Ковекзес."
        )

        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Garden: Leafy", "Сад: листья")
            previewRes = "p4_garden_v1_preview"
            serviceName = ".garden.wallpaper.variations.GardenWallpaperV1"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Garden: Rocky", "Сад: камни")
            previewRes = "p4_garden_v2_preview"
            serviceName = ".garden.wallpaper.variations.GardenWallpaperV2"
        }
        dynamicWallpaper {
            previewName = simplifiedLocaleOf("Garden: Prickly", "Сад: колючки")
            previewRes = "p4_garden_v3_preview"
            serviceName = ".garden.wallpaper.variations.GardenWallpaperV3"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Garden: Leafy", "Сад: листья")
            previewRes = "p4_garden_v1_preview"
            remoteUrl = "p4_garden_v1_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Garden: Rocky", "Сад: камни")
            previewRes = "p4_garden_v2_preview"
            remoteUrl = "p4_garden_v2_preview"
        }
        staticWallpaper {
            previewName = simplifiedLocaleOf("Garden: Prickly", "Сад: колючки")
            previewRes = "p4_garden_v3_preview"
            remoteUrl = "p4_garden_v3_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Appulse

        previewName = simplifiedLocaleOf("Sights From The Sun", "Солнечная система")
        description = simplifiedLocaleOf(
            "Follow along the dance of planets and moons, from the best seat there is: the center of the Solar System.",
            "Солнце – идеальное место, чтобы наблюдать завораживающий танец планет и их спутников."
        )
        previewRes = "p4_sights_preview"

        dynamicWallpaper {
            serviceName = ".sights.wallpapers.variations.SightsWallpaperV1"
        }
        staticWallpaper {
            remoteUrl = "p4_sights_v1_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Mount Pilatus", "Пилатус")
        previewName = simplifiedLocaleOf("Mount Pilatus, Switzerland", "Пилатус (Швейцария)")
        description = simplifiedLocaleOf(
            "Watch as birds glide along the incline of Mount Pilatus, a mountain massif overlooking Lucerne in Central Switzerland",
            "Наблюдайте за полетом птиц вдоль склонов горного массива Пилатус в швейцарских Альпах, откуда открывается захватывающий вид на город Люцерн."
        )
        previewRes = "p4_switzerland_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = 46.980026f,
            longitude = 8.25493f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.switzerland.wallpapers.SwitzerlandWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p4_switzerland_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4
        category = WallpaperCategory.Peaceful

        shortName = simplifiedLocaleOf("Uluru", "Улуру")
        previewName = simplifiedLocaleOf("Uluru, Australia", "Улуру (Австралия)")
        description = simplifiedLocaleOf(
            "Marvel at one of Australia’s most recognisable natural landmarks, Uluru / Ayers Rock.",
            "Полюбуйтесь на скалу Улуру (Айерс Рок) – одну из самых узнаваемых природных достопримечательностей Австралии."
        )
        previewRes = "p4_uluru_preview"
        coordinates = Coordinates.ExactCoordinates(
            latitude = -25.343834f,
            longitude = 131.03667f
        )

        dynamicWallpaper {
            serviceName = ".dioramas.uluru.wallpapers.UluruWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p4_uluru_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P4A
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Eclipse", "Затмение")
        description = simplifiedLocaleOf(
            "Watch the colors ebb and flow as your battery life changes.",
            "Наблюдайте за тем, как переливаются цвета в зависимости от уровня заряда батареи вашего устройства."
        )
        previewRes = "p4a_gradient_preview"

        dynamicWallpaper {
            serviceName = ".gradient.wallpaper.GradientWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p4a_gradient_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P5
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Conveyor Belt", "Конвейерная лента")
        description = simplifiedLocaleOf(
            "Watch as new objects appear, delivered by conveyor belt. Some hint at your phone state, others appear for certain actions.",
            "На конвейерной ленте появляются новые объекты. Некоторые из них сигнализируют о состоянии вашего телефона, другие касаются выполнения определенных действий."
        )
        previewRes = "p5_conveyor_preview"

        dynamicWallpaper {
            serviceName = ".conveyor.wallpaper.ConveyorWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p5_conveyor_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P5
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Stepping Stones", "Пирамида из камней")
        description = simplifiedLocaleOf(
            "This sculpture grows as you unlock your phone. But watch out! It wobbles when your phone tilts.",
            "Эта пирамида из камней растет, если телефон разблокирован. Будьте осторожны: она качается, когда вы наклоняете устройство."
        )
        previewRes = "p5_stack_preview"

        dynamicWallpaper {
            serviceName = ".stack.wallpaper.StackWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p5_stack_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P5
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Moving Shadows", "Движение теней")
        description = simplifiedLocaleOf(
            "Watch sharp shadows transform as the light slowly changes position throughout the day.",
            "Тени от предметов на экране изменяются по мере движения солнца в течение дня."
        )
        previewRes = "p5_sundial_preview"

        dynamicWallpaper {
            serviceName = ".sundial.wallpaper.SundialWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p5_sundial_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P
        category = WallpaperCategory.Fancy

        previewName = simplifiedLocaleOf("Pinball", "Пинбол")
        description = simplifiedLocaleOf(
            "Tilt, tap, or swipe to make the circles move. Inspired by classic pinball.",
            "Чтобы круги двигались, наклоняйте устройство, нажимайте на обои или проводите по ним."
        )
        previewRes = "p_pulley_preview"

        dynamicWallpaper {
            serviceName = ".wallpaper.PulleyWallpaper"
        }
        staticWallpaper {
            remoteUrl = "p_pulley_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Blooming Botanicals", "Цветущие Растения")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )

        dynamicWallpaper {
            previewRes = "p6_blooming_botanicals_v1_preview"
            serviceName =
                ".parallaxflower.multiflower.bloomingbotanicals.BloomingBotanicalsWallpaperService"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v1_preview"
            remoteUrl = "p6_blooming_botanicals_v1_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v2_preview"
            remoteUrl = "p6_blooming_botanicals_v2_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v3_preview"
            remoteUrl = "p6_blooming_botanicals_v3_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v4_preview"
            remoteUrl = "p6_blooming_botanicals_v4_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v5_preview"
            remoteUrl = "p6_blooming_botanicals_v5_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooming_botanicals_v6_preview"
            remoteUrl = "p6_blooming_botanicals_v6_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Bright Blooms", "Яркие Цветы")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )

        dynamicWallpaper {
            previewRes = "p6_blooms_v1_preview"
            serviceName = ".parallaxflower.multiflower.brightblooms.BrightBloomsWallpaperService"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v1_preview"
            remoteUrl = "p6_blooms_v1_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v2_preview"
            remoteUrl = "p6_blooms_v2_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v3_preview"
            remoteUrl = "p6_blooms_v3_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v4_preview"
            remoteUrl = "p6_blooms_v4_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v5_preview"
            remoteUrl = "p6_blooms_v5_preview"
        }
        staticWallpaper {
            previewRes = "p6_blooms_v6_preview"
            remoteUrl = "p6_blooms_v6_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6A
        category = WallpaperCategory.Peaceful

        previewName = simplifiedLocaleOf("Landscapes", "Пейзажи")
        description = simplifiedLocaleOf(
            "Vast landscapes transform into a tapestry of colors.",
            "Обширные пейзажи превращаются в разноцветный гобелен."
        )

        staticWallpaper {
            previewRes = "p6a_landscapes_v1_light_preview"
            remoteUrl = "p6a_landscapes_v1_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_landscapes_v1_dark_preview"
            remoteUrl = "p6a_landscapes_v1_dark_preview"
        }
        staticWallpaper {
            previewRes = "p6a_landscapes_v2_light_preview"
            remoteUrl = "p6a_landscapes_v2_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_landscapes_v2_dark_preview"
            remoteUrl = "p6a_landscapes_v2_dark_preview"
        }
        staticWallpaper {
            previewRes = "p6a_landscapes_v3_light_preview"
            remoteUrl = "p6a_landscapes_v3_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_landscapes_v3_dark_preview"
            remoteUrl = "p6a_landscapes_v3_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6A
        category = WallpaperCategory.Garden

        previewName = simplifiedLocaleOf("Florals", "Цветочные композиции")
        description = simplifiedLocaleOf(
            "Elegant lines form an arrangement of faded florals.",
            "Элегантные линии образуют композицию из увядших цветов."
        )

        staticWallpaper {
            previewRes = "p6a_floral_v1_light_preview"
            remoteUrl = "p6a_floral_v1_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_floral_v1_dark_preview"
            remoteUrl = "p6a_floral_v1_dark_preview"
        }
        staticWallpaper {
            previewRes = "p6a_floral_v2_light_preview"
            remoteUrl = "p6a_floral_v2_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_floral_v2_dark_preview"
            remoteUrl = "p6a_floral_v2_dark_preview"
        }
        staticWallpaper {
            previewRes = "p6a_floral_v3_light_preview"
            remoteUrl = "p6a_floral_v3_light_preview"
        }
        staticWallpaper {
            previewRes = "p6a_floral_v3_dark_preview"
            remoteUrl = "p6a_floral_v3_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Cockatoo", "Какаду")
        description = simplifiedLocaleOf(
            "Notable, Cockatoo lives only in Australia and New Guinea. And now in lives in your phone.",
            "Примечательно, Какаду живет только в Австралии и Новой Гвинее. Теперь она живет и в вашем телефоне."
        )

        staticWallpaper {
            previewRes = "p7_pro_snow_light_preview"
            remoteUrl = "p7_pro_snow_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_pro_snow_dark_preview"
            remoteUrl = "p7_pro_snow_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Pied crow", "Пестрая ворона")
        description = simplifiedLocaleOf(
            "The gait of Pied Crow is serious and full of delight, but at the same time light and fast.",
            "Походка Пестрого Ворона серьёзна и полна достоинства, но при этом легка и быстра."
        )

        staticWallpaper {
            previewRes = "p7_pro_obsidian_light_preview"
            remoteUrl = "p7_pro_obsidian_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_pro_obsidian_dark_preview"
            remoteUrl = "p7_pro_obsidian_dark_preview"
        }
        staticWallpaper {
            previewRes = "p7_obsidian_light_preview"
            remoteUrl = "p7_obsidian_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_obsidian_dark_preview"
            remoteUrl = "p7_obsidian_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Green-winged macaw", "Зеленокрылый ара")
        description = simplifiedLocaleOf(
            "Green-winged macaws are one of the most colored birds. Although, they're invisible in the foliage of trees.",
            "Зеленокрылые ара одни из самых цветастых птиц. Несмотря на это, они невидимы в листве деревьев."
        )

        staticWallpaper {
            previewRes = "p7_pro_lemongrass_light_preview"
            remoteUrl = "p7_pro_lemongrass_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_pro_lemongrass_dark_preview"
            remoteUrl = "p7_pro_lemongrass_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Azure Jay", "Лазурная сойка")
        description = simplifiedLocaleOf(
            "Azure jays have their own language. These are trained birds that have 14 cries in their repertoire for various purposes.",
            "Лазурные сойки имеют свой собственный язык. Это обучаемые птицы, имеющие в своём репертуаре для различных целей 14 криков."
        )

        staticWallpaper {
            previewRes = "p7_snow_light_preview"
            remoteUrl = "p7_snow_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_snow_dark_preview"
            remoteUrl = "p7_snow_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Seagull", "Чайка")
        description = simplifiedLocaleOf(
            "The seagulls are universal. They are the least specialised of all the seabirds, and their body allows for equal adeptness in swimming, flying, and walking.",
            "Чайки - универсальны. Они наименее специализированы из всех морских птиц, и их тело позволяет одинаково хорошо плавать, летать и ходить."
        )

        staticWallpaper {
            previewRes = "p7_haze_light_preview"
            remoteUrl = "p7_haze_light_preview"
        }
        staticWallpaper {
            previewRes = "p7_haze_dark_preview"
            remoteUrl = "p7_haze_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6_EXT
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"
        performance = DynamicWallpaper.Performance.Demanding

        previewName = simplifiedLocaleOf("Flamingo Flower", "Антуриум")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )
        previewRes = "p6_ext_flamingo_flower_preview"

        dynamicWallpaper {
            serviceName = ".flamingoflower.FlamingoFlowerWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p6_ext_flamingo_flower_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6_EXT
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"
        performance = DynamicWallpaper.Performance.Demanding

        previewName = simplifiedLocaleOf("Persian Buttercup", "Лютик азиатский")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )
        previewRes = "p6_ext_persian_buttercup_preview"

        dynamicWallpaper {
            serviceName = ".persianbuttercup.PersianButtercupWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p6_ext_persian_buttercup_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6_EXT
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"
        performance = DynamicWallpaper.Performance.Demanding

        previewName = simplifiedLocaleOf("Maki Dahlia", "Георгина \"Маки\"")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )
        previewRes = "p6_ext_maki_dahlia_preview"

        dynamicWallpaper {
            serviceName = ".makidahlia.MakiDahliaWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p6_ext_maki_dahlia_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P6_EXT
        category = WallpaperCategory.Garden
        author = "Andrew Zuckerman"
        performance = DynamicWallpaper.Performance.Demanding

        previewName = simplifiedLocaleOf("Boat Orchid", "Цимбидиум")
        description = simplifiedLocaleOf(
            "Bloom one by one. Grow together.",
            "Цветут один за другим. Растут вместе."
        )
        previewRes = "p6_ext_boat_orchid_preview"

        dynamicWallpaper {
            serviceName = ".boatorchid.BoatOrchidWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "p6_ext_boat_orchid_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.PFOLD
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Licorice", "Лакрица")
        description = simplifiedLocaleOf(
            "The plumage of elegant birds comes to life as soon as you unlock your phone.",
            "Оперение элегантных птиц оживает, как только вы разблокируете ваш телефон"
        )
        previewRes = "pfold_licorice_preview"

        dynamicWallpaper {
            serviceName = ".licorice.LiveWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "pfold_licorice_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.PFOLD
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Porcelain", "Фарфор")
        description = simplifiedLocaleOf(
            "The plumage of elegant birds comes to life as soon as you unlock your phone.",
            "Оперение элегантных птиц оживает, как только вы разблокируете ваш телефон"
        )
        previewRes = "pfold_porcelain_preview"

        dynamicWallpaper {
            serviceName = ".pixelfold.LiveWallpaperService"
        }
        staticWallpaper {
            remoteUrl = "pfold_porcelain_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7A
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Blue-throated macaw", "Синегорлый ара")
        description = simplifiedLocaleOf(
            "The blue-throated macaw is included in the Red Book, it is known for its intelligence and ability to imitate human speech.",
            "Синегорлый ара включен в Красную книгу, он известен своим интеллектом и способностью подражать человеческой речи."
        )

        staticWallpaper {
            previewRes = "p7a_arctic_light_preview"
            remoteUrl = "p7a_arctic_light_preview"
        }
        staticWallpaper {
            previewRes = "p7a_arctic_dark_preview"
            remoteUrl = "p7a_arctic_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7A
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Andrean condor", "Андский кондор")
        description = simplifiedLocaleOf(
            "The wingspan of the Andean Condor can reach 3 meters, and its weight is 15 kilograms, which makes it one of the largest flying birds in the world.",
            "Размах крыльев Андского кондора может достигать 3 метров, а вес - 15 килограммов, что делает его одной из самых больших летающих птиц в мире."
        )

        staticWallpaper {
            previewRes = "p7a_carbon_light_preview"
            remoteUrl = "p7a_carbon_light_preview"
        }
        staticWallpaper {
            previewRes = "p7a_carbon_dark_preview"
            remoteUrl = "p7a_carbon_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7A
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        shortName = simplifiedLocaleOf("Crane", "Восточный журавль")
        previewName = simplifiedLocaleOf("Grey crowned crane", "Восточный венценосный журавль")
        description = simplifiedLocaleOf(
            "The Grey crowned crane is listed in the Red Book, it can take off to a fairly high altitude up to 10,000 meters.",
            "Восточный венценосный журавль внесен в Красную книгу, он может взлетать на довольно большую высоту до 10 000 метров."
        )

        staticWallpaper {
            previewRes = "p7a_cotton_light_preview"
            remoteUrl = "p7a_cotton_light_preview"
        }
        staticWallpaper {
            previewRes = "p7a_cotton_dark_preview"
            remoteUrl = "p7a_cotton_dark_preview"
        }
    }


    wallpaper {
        parent = WallpaperPacks.P7A
        category = WallpaperCategory.Birdies
        author = "Andrew Zuckerman"

        previewName = simplifiedLocaleOf("Livingstone's turaco", "Турако Ливингстона")
        description = simplifiedLocaleOf(
            "Livingston's Turaco is considered one of the most beautiful birds, after the rain it shines like a diamond",
            "Турако Ливингстона считается одной из самых красивых птиц, после дождя она сияет, как бриллиант"
        )

        staticWallpaper {
            previewRes = "p7a_realr_light_preview"
            remoteUrl = "p7a_realr_light_preview"
        }
        staticWallpaper {
            previewRes = "p7a_realr_dark_preview"
            remoteUrl = "p7a_realr_dark_preview"
        }
    }
}