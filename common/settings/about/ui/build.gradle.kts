plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        settings.about.api()
    }
}

androidNamespace("settings.about.ui")