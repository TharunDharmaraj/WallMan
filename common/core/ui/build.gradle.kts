plugins {
    composeMultiplatformSetup()
}

kotlin {

}
projectDependencies {
    modules {
        core.data()
    }
    external {
        colorata.animateAsLifestyle()

        androidX.lifecycleViewModel()
        androidX.lifecycleCompose()

        compose.navigation()
        compose.material3WindowSize()
        compose.uiUtil()
        compose.uiTooling()
        compose.uiToolingPreview()
        compose.rebugger()
    }

    internal {
        kotlin.collectionsImmutable()
        accompanist.systemUiController()
        compose.materialColorUtilities()
        androidX.palette()

        compose.material()
    }
}

androidNamespace("core.ui")