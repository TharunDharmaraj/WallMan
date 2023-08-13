plugins {
    multiplatformSetup()
    serialization()
}

projectDependencies(commonTestBlock = {}) {
    modules {
        core.data()
        categories.api()
    }
    internal {
        compose.runtime()
        compose.material3()
    }
}

androidNamespace("wallpapers.api")