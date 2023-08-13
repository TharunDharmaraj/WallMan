plugins {
    multiplatformSetup()
    serialization()
}

projectDependencies(commonTestBlock = {}) {
    modules {
        core.data()
        core.di()
        wallpapers.api()

        categories.api()
    }
}

androidNamespace("wallpapers.impl")