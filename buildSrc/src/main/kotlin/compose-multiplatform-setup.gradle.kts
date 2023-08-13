plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    sourceSets {
        val commonMain by getting {
            dependencies {
                internal {
                    compose.runtime()
                    compose.material3()
                }
            }
        }
    }
}

android {
    compileSdk = App.compileSdk
    defaultConfig {
        minSdk = App.minSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
            assets.srcDirs("src/androidMain/assets", "src/commonMain/assets")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = LibraryVersions.Compose.compiler
    }
}

setupKotlin()