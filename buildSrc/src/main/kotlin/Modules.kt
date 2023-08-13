import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.modules(block: Modules.() -> Unit) {
    val modules = Modules {
        implementation(project(it))
    }
    modules.block()
}

fun DependencyHandlerScope.projectModules(block: Modules.() -> Unit) {
    val modules = Modules {
        "implementation"(project(it))
    }
    modules.block()
}

class Modules(private val applier: (moduleName: String) -> Unit) {


    fun shared() = applier(":shared")

    val core = Core()

    inner class Core {
        private val prefix = ":common:core"
        fun di() = applier("$prefix:di")
        fun data() = applier("$prefix:data")
        fun ui() = applier("$prefix:ui")
        fun impl() = applier("$prefix:impl")
    }

    val greeting = Greeting()
    inner class Greeting {
        private val prefix = ":common:greeting"

        fun api() = applier("$prefix:api")
        fun ui() = applier("$prefix:ui")
    }

    val wallpapers = Wallpapers()

    inner class Wallpapers {
        private val prefix = ":common:wallpapers"
        fun api() = applier("$prefix:api")
        fun impl() = applier("$prefix:impl")
        fun ui() = applier("$prefix:ui")
    }

    val categories = Categories()

    inner class Categories {
        private val prefix = ":common:categories"
        fun api() = applier("$prefix:api")
        fun ui() = applier("$prefix:ui")
    }

    val widget = Widget()

    inner class Widget {
        private val prefix = ":common:widget"
        fun api() = applier("$prefix:api")
        fun impl() = applier("$prefix:impl")
        fun ui() = applier("$prefix:ui")
        fun uiWidget() = applier("$prefix:ui_widget")
    }

    val settings = Settings()

    inner class Settings {
        val overview = Overview()

        inner class Overview {
            private val prefix = ":common:settings:overview"
            fun api() = applier("$prefix:api")
            fun ui() = applier("$prefix:ui")
        }

        val memory = Memory()

        inner class Memory {
            private val prefix = ":common:settings:memory"
            fun api() = applier("$prefix:api")
            fun ui() = applier("$prefix:ui")
        }

        val mirror = Mirror()

        inner class Mirror {
            private val prefix = ":common:settings:mirror"
            fun api() = applier("$prefix:api")
            fun ui() = applier("$prefix:ui")
        }

        val about = About()

        inner class About {
            private val prefix = ":common:settings:about"
            fun api() = applier("$prefix:api")
            fun ui() = applier("$prefix:ui")
        }
    }
}