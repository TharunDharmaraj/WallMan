package com.colorata.wallman.shared

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.greeting.api.GreetingDestination
import com.colorata.wallman.wallpapers.MainDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : GraphActivity() {

    override val isUiLocked = MutableStateFlow(true)

    private var startDestination = Destinations.GreetingDestination()

    @Composable
    override fun Content() {
        App(startDestination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {

            graph.coreModule.applicationSettings.nullableSettings()
                .first { settings -> settings != null }

            val proceededGreeting =
                graph.coreModule.applicationSettings.settings()
                    .value.proceededGreeting

            startDestination =
                if (proceededGreeting) Destinations.MainDestination()
                else Destinations.GreetingDestination()

            isUiLocked.value = false
        }
    }
}