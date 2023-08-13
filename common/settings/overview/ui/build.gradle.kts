plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        wallpapers.api()

        settings.overview.api()
        settings.memory.api()
        settings.mirror.api()
        settings.about.api()
    }
}

androidNamespace("settings.overview.ui")