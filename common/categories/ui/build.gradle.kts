plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        wallpapers.api()
        wallpapers.ui()

        categories.api()
    }
}

androidNamespace("categories.ui")