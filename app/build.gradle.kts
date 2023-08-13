import com.colorata.wallman.buildSrc.AppDefaultPlugin

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.baselineprofile")
}

apply<AppDefaultPlugin>()

android {

    kotlinOptions.apply {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.9.0"
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}"
        )
    }
}

dependencies {
    implementation("androidx.profileinstaller:profileinstaller:1.3.0")
    testImplementation(Libraries.Test.junit)
    androidTestImplementation(Libraries.Test.androidXJunit)
    androidTestImplementation(Libraries.Compose.uiTest)
    "baselineProfile"(project(":app:baselineprofile"))

    debugImplementation(Libraries.Compose.uiToolingManifest)
    debugImplementation(Libraries.Compose.uiTooling)
    implementation(Libraries.Compose.uiToolingPreview)
    debugImplementation(Libraries.Compose.uiTestManifest)

    projectModules {
        shared()

        core.data()
        core.impl()
        core.di()
        core.data()

        settings.about.ui()

        wallpapers.api()

        widget.impl()
        widget.uiWidget()
    }
}

gradle.taskGraph.whenReady {
    val file = file(rootDir.absolutePath + "/app/build/outputs/apk/release/app-release.apk")
    copy {
        from(file.absolutePath)
        into(rootDir.absolutePath)
    }
    println(file.absolutePath)
}