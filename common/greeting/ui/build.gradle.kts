plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        greeting.api()

        wallpapers.api()
    }
}

androidNamespace("greeting.ui")