plugins {
    multiplatformSetup()
}

projectDependencies(
    androidMainBlock = {
        internal {
            androidX.work()
            compose.glance()
            compose.glanceAppWidget()
        }
    }) {
    modules {
        core.data()
        core.di()

        widget.api()
        widget.uiWidget()

        wallpapers.api()
    }
}

androidNamespace("widget.impl")