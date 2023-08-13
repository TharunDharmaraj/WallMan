plugins {
    multiplatformSetup()
    serialization()
}

projectDependencies {
    modules {
        wallpapers.api()

        widget.api()
        core.data()
    }
}

androidNamespace("core.di")