import org.gradle.plugin.use.PluginDependenciesSpec

fun PluginDependenciesSpec.multiplatformSetup() {
    id("multiplatform-setup")
}

fun PluginDependenciesSpec.composeMultiplatformSetup() {
    id("compose-multiplatform-setup")
}

fun PluginDependenciesSpec.serialization() {
    id("org.jetbrains.kotlin.plugin.serialization")
}