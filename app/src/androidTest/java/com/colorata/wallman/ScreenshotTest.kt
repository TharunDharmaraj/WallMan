package com.colorata.wallman

import android.app.Activity
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.io.FileOutputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun mainScreen() {
        rule.takeScreenshot("main.png")
    }

    @Test
    fun detailsScreen() {
        rule.onNode(hasTestTag("Wallpaper0")).performClick()
        rule.takeScreenshot("detail.png")
    }

    @Test
    fun categoriesScreen() {
        rule.onNode(hasTestTag("CategoriesList")).performClick()
        rule.takeScreenshot("categories.png")
    }

    private fun ComposeContentTestRule.takeScreenshot(name: String) {
        onRoot()
            .captureToImage()
            .asAndroidBitmap()
            .save(name)
    }

    private fun Bitmap.save(name: String) {
        val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath
        FileOutputStream("$path/$name").use {
            compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}