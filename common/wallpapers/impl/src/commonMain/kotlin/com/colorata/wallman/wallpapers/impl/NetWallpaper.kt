package com.colorata.wallman.wallpapers.impl

import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.categories.api.WallpaperCategoryLocale
import com.colorata.wallman.core.data.Coordinates
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.simplifiedLocaleOf
import com.colorata.wallman.wallpapers.AndroidVersionCompatibilityChecker
import com.colorata.wallman.wallpapers.DynamicWallpaper
import com.colorata.wallman.wallpapers.StaticWallpaper
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpaperPacks
import com.colorata.wallman.wallpapers.baseWallpapers
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

@Serializable
internal data class NetWallpaper(
    val previewName: NetPolyglot? = null,
    val previewRes: String? = null,
    val shortName: NetPolyglot? = null,
    val description: NetPolyglot? = null,
    val coordinates: Coordinates.ExactCoordinates? = null,
    val dynamicWallpapers: List<NetDynamicWallpaper> = listOf(),
    val staticWallpapers: List<NetStaticWallpaper>,
    val parentId: Int,
    val categoryId: Int,
    val author: String
)

internal fun NetWallpaper.toWallpaper(
    categories: List<NetWallpaperCategory>, wallpaperPacks: List<NetWallpaperPack>
): WallpaperI {
    val parent = wallpaperPacks.first { it.id == parentId }.toWallpaperPack()
    val category = categories.first { it.id == categoryId }.toWallpaperCategory()
    return WallpaperI(
        dynamicWallpapers.map { wallpaper ->
            wallpaper.toDynamicWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = description,
                previewRes = previewRes,
                parent = parent,
                coordinates = coordinates
            )
        }.toImmutableList(),
        staticWallpapers.map { wallpaper ->
            wallpaper.toStaticWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = description,
                previewRes = previewRes,
                parent = parent,
                coordinates = coordinates
            )
        }.toImmutableList(),
        author = author,
        category = category,
        parent = parent
    )
}

@Serializable
internal data class NetDynamicWallpaper(
    val previewName: NetPolyglot? = null,
    val shortName: NetPolyglot? = previewName,
    val description: NetPolyglot? = null,
    val serviceName: String,
    val previewRes: String? = null,
    val coordinates: Coordinates.ExactCoordinates? = null,
    val performance: DynamicWallpaper.Performance = DynamicWallpaper.Performance.Normal
)

internal fun NetDynamicWallpaper.toDynamicWallpaper(
    previewName: NetPolyglot?,
    shortName: NetPolyglot?,
    description: NetPolyglot?,
    previewRes: String?,
    parent: WallpaperPacks,
    coordinates: Coordinates.ExactCoordinates?
): DynamicWallpaper {
    return DynamicWallpaper(
        previewName = previewName?.toPolyglot() ?: this.previewName?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        shortName = shortName?.toPolyglot() ?: this.shortName?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        description = description?.toPolyglot() ?: this.description?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        parent = parent,
        previewRes = previewRes ?: this.previewRes ?: "",
        coordinates = coordinates ?: this.coordinates,
        serviceName = serviceName,
        compatibilityChecker = AndroidVersionCompatibilityChecker(parent.minSdk),
        performance = performance
    )
}

@Serializable
internal data class NetStaticWallpaper(
    val previewName: NetPolyglot? = null,
    val shortName: NetPolyglot? = previewName,
    val description: NetPolyglot? = null,
    val remoteUrl: String,
    val previewRes: String? = null,
    val coordinates: Coordinates.ExactCoordinates? = null
)

internal fun NetStaticWallpaper.toStaticWallpaper(
    previewName: NetPolyglot?,
    shortName: NetPolyglot?,
    description: NetPolyglot?,
    previewRes: String?,
    parent: WallpaperPacks,
    coordinates: Coordinates.ExactCoordinates?
): StaticWallpaper {
    return StaticWallpaper(
        previewName = previewName?.toPolyglot() ?: this.previewName?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        shortName = shortName?.toPolyglot() ?: this.shortName?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        description = description?.toPolyglot() ?: this.description?.toPolyglot()
        ?: simplifiedLocaleOf(""),
        parent = parent,
        previewRes = previewRes ?: this.previewRes ?: "",
        coordinates = coordinates ?: this.coordinates,
        remoteUrl = remoteUrl.substringBeforeLast("."),
        remoteUrlExtension = if ("." in remoteUrl) "." + remoteUrl.substringAfterLast(".") else ".png"
    )
}

@Serializable
internal data class NetWallpaperCategory(
    val name: NetPolyglot, val description: NetPolyglot, val id: Int
)

internal fun NetWallpaperCategory.toWallpaperCategory(): WallpaperCategory {
    return WallpaperCategory(
        WallpaperCategoryLocale(
            name = name.toPolyglot(),
            description = description.toPolyglot(),
        ),
        id = id
    )
}

@Serializable
internal data class NetPolyglot(
    val en: String, val ru: String = en
)

internal fun NetPolyglot.toPolyglot(): Polyglot {
    return simplifiedLocaleOf(english = en, russian = ru)
}

@Serializable
internal data class NetWallpaperPack(
    val urlPart: String,
    val previewName: NetPolyglot,
    val description: NetPolyglot = previewName,
    val url: String,
    val id: Int,
    val packageName: String,
    val packageServiceName: String = packageName,
    val checksum: Long,
    val includesDynamic: Boolean = true,
    val previewRes: String,
    val minSdk: Int = 0
)

internal fun NetWallpaperPack.toWallpaperPack(): WallpaperPacks {
    return WallpaperPacks(
        urlPart = urlPart,
        url = url,
        previewName = previewName.toPolyglot(),
        checksum = checksum,
        id = id,
        packageName = packageName,
        packageServiceName = packageServiceName,
        previewRes = previewRes,
        minSdk = minSdk,
        includesDynamic = includesDynamic,
        description = description.toPolyglot()
    )
}

internal fun WallpaperPacks.toNetWallpaperPack(): NetWallpaperPack {
    return NetWallpaperPack(
        urlPart = urlPart,
        checksum = checksum,
        id = id,
        packageName = packageName,
        packageServiceName = packageServiceName,
        previewName = previewName.toNetPolyglot(),
        previewRes = previewRes,
        url = url,
        minSdk = minSdk,
        includesDynamic = includesDynamic,
        description = description.toNetPolyglot()
    )
}

internal fun WallpaperCategory.toNetWallpaperCategory(): NetWallpaperCategory {
    return NetWallpaperCategory(
        name = locale.name.toNetPolyglot(),
        description = locale.description.toNetPolyglot(),
        id = id
    )
}

internal fun WallpaperI.toNetWallpaper(): NetWallpaper {
    val previewName = if (baseWallpapers().map { it.previewName }
            .toSet().size == 1) staticWallpapers[0].previewName.toNetPolyglot() else null
    val previewRes = if (baseWallpapers().map { it.previewRes }
            .toSet().size == 1) staticWallpapers[0].previewRes + ".jpg" else null
    val description = if (baseWallpapers().map { it.description }
            .toSet().size == 1) staticWallpapers[0].description.toNetPolyglot() else null
    val shortName = if (baseWallpapers().map { it.shortName }
            .toSet().size == 1) staticWallpapers[0].shortName.toNetPolyglot() else null
    val coordinates = if (baseWallpapers().map { it.coordinates }
            .toSet().size == 1) staticWallpapers[0].coordinates as? Coordinates.ExactCoordinates else null
    val dynamicWallpapers =
        dynamicWallpapers.map {
            it.toNetDynamicWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = description,
                previewRes = previewRes,
                coordinates = coordinates,
                performance = it.performance
            )
        }
    val staticWallpapers =
        staticWallpapers.map {
            it.toNetStaticWallpaper(
                previewName = previewName,
                shortName = shortName,
                description = description,
                previewRes = previewRes,
                coordinates = coordinates
            )
        }
    return NetWallpaper(
        previewName = previewName,
        previewRes = previewRes,
        shortName = shortName,
        description = description,
        author = author,
        categoryId = category.id,
        coordinates = coordinates,
        parentId = parent.id,
        staticWallpapers = staticWallpapers,
        dynamicWallpapers = dynamicWallpapers
    )
}

internal fun DynamicWallpaper.toNetDynamicWallpaper(
    previewName: NetPolyglot?,
    shortName: NetPolyglot?,
    description: NetPolyglot?,
    previewRes: String?,
    coordinates: Coordinates?,
    performance: DynamicWallpaper.Performance
): NetDynamicWallpaper {
    return NetDynamicWallpaper(
        serviceName = serviceName,
        previewName = if (previewName != null) null else this.previewName.toNetPolyglot(),
        shortName = if (shortName != null) null else this.shortName.toNetPolyglot(),
        description = if (description != null) null else this.description.toNetPolyglot(),
        previewRes = if (previewRes != null) null else this.previewRes + ".jpg",
        coordinates = if (coordinates != null) null else this.coordinates as? Coordinates.ExactCoordinates,
        performance = performance
    )
}

internal fun StaticWallpaper.toNetStaticWallpaper(
    previewName: NetPolyglot?,
    shortName: NetPolyglot?,
    description: NetPolyglot?,
    previewRes: String?,
    coordinates: Coordinates?
): NetStaticWallpaper {
    return NetStaticWallpaper(
        remoteUrl = remoteUrl,
        previewName = if (previewName != null) null else this.previewName.toNetPolyglot(),
        shortName = if (shortName != null) null else this.shortName.toNetPolyglot(),
        description = if (description != null) null else this.description.toNetPolyglot(),
        previewRes = if (previewRes != null) null else this.previewRes + ".jpg",
        coordinates = if (coordinates != null) null else this.coordinates as? Coordinates.ExactCoordinates,
    )
}

internal fun Polyglot.toNetPolyglot(): NetPolyglot {
    return NetPolyglot(
        languageMap["en"] ?: "",
        languageMap["ru"] ?: ""
    )
}