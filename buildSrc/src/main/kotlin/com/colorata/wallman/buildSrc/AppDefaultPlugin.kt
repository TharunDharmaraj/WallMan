package com.colorata.wallman.buildSrc

import App
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AppDefaultPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidComponents.finalizeDsl {
            it.namespace = App.id
            it.compileSdk = App.compileSdk
            it.packagingOptions {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
            it.signingConfigs {
                create("release") {
                    storeFile = project.file("${project.rootProject.projectDir}/key.jks")
                    storePassword = "123456"
                    keyAlias = "key"
                    keyPassword = "123456"
                }
            }
            it.defaultConfig {
                applicationId = App.id
                minSdk = App.minSdk
                targetSdk = App.targetSdk
                versionCode = App.versionCode
                versionName = App.versionName
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }
                signingConfig = it.signingConfigs.getByName("release")
            }

            it.buildTypes {
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    signingConfig = it.signingConfigs.getByName("release")
                    proguardFiles(
                        it.getDefaultProguardFile("proguard-android.txt"),
                        "proguard-rules.pro"
                    )
                }
                getByName("nonMinifiedRelease") {
                    proguardFiles(
                        it.getDefaultProguardFile("proguard-android.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            it.buildFeatures {
                compose = true
                buildConfig = false
            }
            it.composeOptions {
                kotlinCompilerExtensionVersion = LibraryVersions.Compose.compiler
            }
            it.compileOptions {
                sourceCompatibility = JavaVersion.VERSION_18
                targetCompatibility = JavaVersion.VERSION_18
            }
        }
    }
}