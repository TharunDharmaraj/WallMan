plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        settings.mirror.api()
    }
}

androidNamespace("settings.mirror.ui")