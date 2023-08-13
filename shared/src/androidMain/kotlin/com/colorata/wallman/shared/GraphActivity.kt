package com.colorata.wallman.shared

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.colorata.wallman.core.data.module.loadables
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.di.LocalGraph
import com.colorata.wallman.core.di.impl.applyActivity
import com.colorata.wallman.core.ui.theme.WallManTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class GraphActivity : ComponentActivity() {

    @Composable
    abstract fun Content()

    private val graph by lazy {
        (application as WallManApp).graph
    }

    open val isUiLocked = MutableStateFlow(false)

    @SuppressLint("RememberReturnType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        graph.applyActivity(this)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            graph.coreModule.logger.throwable(throwable)
            graph.coreModule.intentHandler.goToActivity(this::class)
            finish()
        }

        lifecycleScope.launch {
            isUiLocked.first { locked -> !locked }

            setContent {
                CompositionLocalProvider(LocalGraph provides graph) {
                    WallManTheme {
                        Content()
                    }
                }
            }
        }
        installSplashScreen().apply {
            setKeepOnScreenCondition(condition = { false })
        }
    }

    override fun onStart() {
        super.onStart()
        graph.coreModule.loadables.forEach { it.load() }
    }

    override fun onResume() {
        super.onResume()
        graph.coreModule.appsProvider.update()
    }

    override fun onStop() {
        super.onStop()
        graph.coreModule.loadables.forEach { it.unload() }
    }
}