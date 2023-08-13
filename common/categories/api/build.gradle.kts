plugins {
    multiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
    }
}

androidNamespace("categories.api")