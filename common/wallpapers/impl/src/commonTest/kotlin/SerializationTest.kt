import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.Coordinates
import com.colorata.wallman.core.data.serialization.Json
import com.colorata.wallman.core.data.simplifiedLocaleOf
import com.colorata.wallman.wallpapers.WallpaperPacks
import com.colorata.wallman.wallpapers.createWallpapers
import com.colorata.wallman.wallpapers.impl.NetPolyglot
import com.colorata.wallman.wallpapers.impl.NetWallpaper
import com.colorata.wallman.wallpapers.impl.NetWallpaperCategory
import com.colorata.wallman.wallpapers.impl.NetWallpaperConfiguration
import com.colorata.wallman.wallpapers.impl.NetWallpaperPack
import com.colorata.wallman.wallpapers.impl.toNetWallpaper
import com.colorata.wallman.wallpapers.impl.toNetWallpaperCategory
import com.colorata.wallman.wallpapers.impl.toNetWallpaperPack
import com.colorata.wallman.wallpapers.impl.toWallpaper
import com.colorata.wallman.wallpapers.impl.toWallpaperCategory
import com.colorata.wallman.wallpapers.impl.toWallpaperPack
import com.colorata.wallman.wallpapers.walls
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Test
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun resource(path: String) = File("src/commonTest/resources/$path")

fun readFromResources(path: String): String =
    resource(path).readText()

class SerializationTest {

    private val netWallpaperPack = NetWallpaperPack(
        urlPart = "P.apk",
        checksum = 10000,
        id = 0,
        previewRes = "p_wallpaper_preview.png",
        packageName = "com.google.pixel.p",
        previewName = NetPolyglot("WallpaperPack p", "Пак обоев п"),
        packageServiceName = "com.google.pixel.p22",
        url = "P"
    )

    private val netWallpaperCategory = NetWallpaperCategory(
        NetPolyglot("Appulse", "Парад планет"),
        NetPolyglot(
            "Explore whole Solar System with Your device.",
            "Исследуйте всю Солнечную систему с Вашим устройством."
        ),
        id = 0
    )

    private val wallpapers by createWallpapers {
        wallpaper {
            previewName = simplifiedLocaleOf("PreviewName", "Превью")
            shortName = simplifiedLocaleOf("ShortName", "Короткое превью")
            description = simplifiedLocaleOf("Description", "Описание")
            author = "Author"
            previewRes = "p_wallpaper_preview.png"
            category = WallpaperCategory.Appulse
            parent = WallpaperPacks.P
            dynamicWallpaper {
                serviceName = ".p.wallpaper"
            }
            staticWallpaper {
                remoteUrl = "p_wallpaper"
            }
        }
        wallpaper {
            author = "Author"
            category = WallpaperCategory.Appulse
            parent = WallpaperPacks.P
            dynamicWallpaper {
                previewName = simplifiedLocaleOf("AA", "QQ")
                shortName = simplifiedLocaleOf("BB", "CC")
                description = simplifiedLocaleOf("DD", "EE")
                previewRes = "aaaa.png"
                serviceName = ".p.p"
            }
            staticWallpaper {
                previewName = simplifiedLocaleOf("DD", "FF")
                shortName = simplifiedLocaleOf("OO", "LL")
                description = simplifiedLocaleOf("EE", "NN")
                previewRes = "dddd.png"
                remoteUrl = "dddd_d"
            }
        }
        wallpaper {
            previewName = simplifiedLocaleOf("PreviewName", "Превью")
            shortName = simplifiedLocaleOf("ShortName", "Короткое превью")
            description = simplifiedLocaleOf("Description", "Описание")
            author = "Author"
            previewRes = "p_wallpaper_preview.png"
            category = WallpaperCategory.Appulse
            parent = WallpaperPacks.P
            coordinates = Coordinates.ExactCoordinates(10f, 10f)
            dynamicWallpaper {
                serviceName = ".p.wallpaper"
            }
            staticWallpaper {
                remoteUrl = "p_wallpaper"
            }
        }
    }

    private val wallpaperPacks =listOf(
        WallpaperPacks.P,
        WallpaperPacks.P1,
        WallpaperPacks.P2,
        WallpaperPacks.P3,
        WallpaperPacks.P4,
        WallpaperPacks.P4A,
        WallpaperPacks.P5,
        WallpaperPacks.P6,
        WallpaperPacks.P6_EXT,
        WallpaperPacks.P6A,
        WallpaperPacks.P7,
        WallpaperPacks.P7A,
        WallpaperPacks.PFOLD
    )

    private val wallpaperCategories = listOf(
        WallpaperCategory.Appulse,
        WallpaperCategory.Wonders,
        WallpaperCategory.Peaceful,
        WallpaperCategory.Fancy,
        WallpaperCategory.Garden,
        WallpaperCategory.Birdies,
    )

    @Test
    fun wallpaperPacksSerialization() {
        val initial = readFromResources("simple_wallpaper_net.json")
        val decodedNet = Json.decodeFromString<NetWallpaper>(initial)
        val wallpaper =
            decodedNet.toWallpaper(listOf(netWallpaperCategory), listOf(netWallpaperPack))
        assertEquals(wallpapers[0], wallpaper)
    }

    @Test
    fun separatedSerialization() {
        val initial = readFromResources("separated_wallpaper_net.json")
        val decodedNet = Json.decodeFromString<NetWallpaper>(initial)
        val wallpaper =
            decodedNet.toWallpaper(listOf(netWallpaperCategory), listOf(netWallpaperPack))
        assertEquals(wallpapers[1], wallpaper)
    }

    @Test
    fun sharedCoordinatesSerialization() {
        val initial = readFromResources("shared_coordinates.json")
        val decodedNet = Json.decodeFromString<NetWallpaper>(initial)
        val wallpaper =
            decodedNet.toWallpaper(listOf(netWallpaperCategory), listOf(netWallpaperPack))
        assertEquals(wallpapers[2], wallpaper)
    }

    @Test
    fun generateNetWallpapers() {
        val outFile = resource("wallpapers.json")
        val json = Json.encodeToString(walls.map { it.toNetWallpaper() })
        outFile.writeText(json)
    }

    @Test
    fun generateNetWallpaperPacks() {
        val outFile = resource("packs.json")
        val json = Json.encodeToString(wallpaperPacks.map { it.toNetWallpaperPack() })
        outFile.writeText(json)
    }

    @Test
    fun generateNetWallpaperCategories() {
        val outFile = resource("categories.json")
        val json =
            Json.encodeToString(wallpaperCategories.map { it.toNetWallpaperCategory() })
        outFile.writeText(json)
    }

    @Test
    fun generateNetWallpapersConfiguration() {
        val outFile = resource("configuration.json")
        val json = Json.encodeToString(
            NetWallpaperConfiguration(
                api = 5,
                categories = "categories.json",
                packs = "packs.json",
                wallpapers = "wallpapers.json",
                resources = "resources/",
                urls = listOf("https://sam.nl.tab.digital/s/wqZaeixFAsDEdGe/download?path=/")
            )
        )
        outFile.writeText(json)
    }

    @Test
    fun generate() {
        generateNetWallpapers()
        generateNetWallpaperCategories()
        generateNetWallpaperPacks()
    }

    @Test
    fun wallpaperPacksDeserialization() {
        generateNetWallpaperPacks()
        val initial = readFromResources("packs.json")
        val wallpapers = Json.decodeFromString<List<NetWallpaperPack>>(initial)
        assertContentEquals(wallpaperPacks, wallpapers.map { it.toWallpaperPack() })
    }

    @Test
    fun wallpaperCategoriesDeserialization() {
        generateNetWallpaperCategories()
        val initial = readFromResources("categories.json")
        val wallpapers = Json.decodeFromString<List<NetWallpaperCategory>>(initial)
        assertContentEquals(wallpaperCategories, wallpapers.map { it.toWallpaperCategory() })
    }

    @Test
    fun wallpapersDeserialization() {
        generateNetWallpapers()
        val categories = categories()
        val wallpaperPacks = wallpaperPacks()
        val initial = readFromResources("wallpapers.json")
        val wallpapers = Json.decodeFromString<List<NetWallpaper>>(initial)
        val converted = wallpapers.map { it.toWallpaper(categories, wallpaperPacks) }

        assertContentEquals(walls, converted)
    }

    private fun categories(): List<NetWallpaperCategory> {
        val json = readFromResources("categories.json")
        return Json.decodeFromString<List<NetWallpaperCategory>>(json)
    }

    private fun wallpaperPacks(): List<NetWallpaperPack> {
        val json = readFromResources("packs.json")
        return Json.decodeFromString<List<NetWallpaperPack>>(json)
    }
}