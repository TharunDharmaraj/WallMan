plugins {
    composeMultiplatformSetup()
}

projectDependencies {
    modules {
        core.data()
        core.ui()

        widget.api()
    }
}

androidNamespace("widget.ui")