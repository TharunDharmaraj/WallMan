pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "WallMan"

rootDir
    .walk()
    .filter {
        it.name != "buildSrc"
                && it.isDirectory
                && file("${it.absolutePath}/build.gradle.kts").exists()
    }
    .forEach {
        val calculated = it.absolutePath.replace(rootDir.absolutePath, "").replace("/", ":")
        include(calculated)
    }