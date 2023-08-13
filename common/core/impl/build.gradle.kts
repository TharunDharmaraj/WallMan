plugins {
    multiplatformSetup()
    serialization()
}

projectDependencies {
    modules {
        core.data()
        core.di()

        wallpapers.impl()
        wallpapers.api()

        widget.api()
        widget.impl()
    }

    internal {
        androidX.dataStore()
        ktor.library()

        compose.material3WindowSize()
    }
}

androidNamespace("core.impl")