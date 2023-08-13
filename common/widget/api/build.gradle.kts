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

        wallpapers.api()
    }
}

androidNamespace("widget.api")