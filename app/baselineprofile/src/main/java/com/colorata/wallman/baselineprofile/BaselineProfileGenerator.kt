package com.colorata.wallman.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the Generate Baseline Profile run configuration,
 * or directly with `generateBaselineProfile` Gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile -Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect("com.colorata.wallman", maxIterations = 3) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup. But you can also navigate and scroll
            // through your most important UI.

            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            runBenchmark()
            // For example:
            // 1. Wait until the content is asynchronously loaded
            // 2. Scroll the feed content
            // 3. Navigate to detail screen

            // Check UiAutomator documentation for more information how to interact with the app.
            // https://d.android.com/training/testing/other-components/ui-automator
        }
    }
}

fun MacrobenchmarkScope.runBenchmark() {
    device.wait(Until.hasObject(By.res("WallpaperList")), 5_000)
    val contentList = device.findObject(By.res("WallpaperList"))
    contentList.wait(Until.hasObject(By.res("Wallpaper")), 5_000)

    val snackList = device.findObject(By.res("WallpaperList"))
    // Set gesture margin to avoid triggering gesture navigation.
    snackList.setGestureMargin(device.displayWidth)
    snackList.fling(Direction.DOWN)

    val snacks = snackList.findObjects(By.res("Wallpaper"))
    // Select snack from the list based on running iteration.
    val index = (iteration ?: 0) % snacks.size
    snacks[index].click()
    // Wait until the screen is gone = the detail is shown.
    device.wait(Until.gone(By.res("Wallpaper")), 5_000)
}