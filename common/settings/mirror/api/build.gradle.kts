plugins {
    multiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
    }
}

androidNamespace("settings.mirror.api")