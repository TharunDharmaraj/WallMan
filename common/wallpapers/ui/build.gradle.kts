plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        wallpapers.api()
        core.data()
        core.ui()
    }
}

androidNamespace("wallpapers.ui")