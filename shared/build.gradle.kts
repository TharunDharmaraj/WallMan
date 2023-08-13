plugins {
    composeMultiplatformSetup()
}

projectDependencies(androidUnitTestBlock = {}, commonTestBlock = {}) {
    internal {
        compose.activity()
        compose.navigation()

        androidX.activity()
        androidX.splashscreen()
        androidX.startup()
    }
    modules {
        core.impl()
        core.data()
        core.ui()
        core.di()

        greeting.api()
        greeting.ui()

        wallpapers.ui()
        wallpapers.api()

        categories.api()
        categories.ui()

        widget.api()
        widget.impl()
        widget.ui()

        settings.overview.api()

        settings.overview.ui()
        settings.about.ui()
        settings.memory.ui()
        settings.mirror.ui()
    }
}

androidNamespace("shared")