plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        wallpapers.api()
        wallpapers.ui()
        settings.memory.api()
    }
}

androidNamespace("settings.memory.ui")