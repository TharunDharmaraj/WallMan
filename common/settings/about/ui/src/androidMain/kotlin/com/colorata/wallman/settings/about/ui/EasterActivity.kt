package com.colorata.wallman.settings.about.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.colorata.wallman.core.ui.theme.WallManTheme
import com.colorata.wallman.settings.overview.ui.FancyTextScreen
import java.util.*

class EasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WallManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FancyTextScreen(text = remember {
                        "${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}: ${
                            Calendar.getInstance().get(
                                Calendar.MINUTE
                            )
                        }"
                    })
                }
            }
        }
    }
}