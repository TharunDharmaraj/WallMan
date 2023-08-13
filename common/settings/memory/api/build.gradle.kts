plugins {
    multiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
    }
}

androidNamespace("settings.memory.api")