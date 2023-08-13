plugins {
    multiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
    }
}

androidNamespace("settings.overview.api")