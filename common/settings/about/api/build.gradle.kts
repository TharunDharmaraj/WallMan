plugins {
    multiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
    }
}

androidNamespace("settings.about.api")