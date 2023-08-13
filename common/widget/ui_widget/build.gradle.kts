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

        wallpapers.api()
    }
}

androidNamespace("widget.ui_widget")