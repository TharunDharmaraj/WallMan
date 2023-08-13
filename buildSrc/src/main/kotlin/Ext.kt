import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.android
import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.kotlin
import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.sourceSets
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.setupKotlin() {
    tasks.withType(KotlinCompile::class.java) {
        kotlinOptions.jvmTarget = "18"
        kotlinOptions.composeReports(project.buildDir.absolutePath)
        kotlinOptions.experimentalContextReceivers()
    }
}

fun KotlinCommonOptions.composeReports(path: String) {
    freeCompilerArgs += listOf(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$path"
    )
    freeCompilerArgs += listOf(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.9.0"
    )
}

fun KotlinCommonOptions.experimentalContextReceivers() {
    freeCompilerArgs += listOf("-Xcontext-receivers")
}

fun Project.projectDependencies(
    androidMainBlock: KotlinDependencyHandler.() -> Unit = {},
    androidUnitTestBlock: (KotlinDependencyHandler.() -> Unit)? = null,
    commonTestBlock: (KotlinDependencyHandler.() -> Unit)? = null,
    commonMainBlock: KotlinDependencyHandler.() -> Unit
) {
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    commonMainBlock()
                }
            }
            if (commonTestBlock != null) {
                val commonTest by getting {
                    dependsOn(commonMain)
                    dependencies {
                        implementation(kotlin("test"))
                        internal {
                            test.junit()
                        }
                        commonTestBlock()
                    }
                }
            }
            val androidMain by getting {
                dependsOn(commonMain)
                dependencies {
                    androidMainBlock()
                }
            }
            if (androidUnitTestBlock != null) {
                val androidUnitTest by getting {
                    dependsOn(androidMain)
                    dependencies {
                        implementation(kotlin("test"))
                        internal {
                            test.junit()
                            test.androidXJunit()
                        }
                        androidMainBlock()
                    }
                }
            }
        }
    }
}

fun KotlinDependencyHandler.external(block: Libs.() -> Unit) {
    val libs = Libs { api(it) }
    libs.block()
}

fun KotlinDependencyHandler.internal(block: Libs.() -> Unit) {
    val libs = Libs { implementation(it) }
    libs.block()
}

fun Project.androidNamespace(namespace: String) {
    android {
        this.namespace = "com.colorata.wallman.$namespace"
    }
}