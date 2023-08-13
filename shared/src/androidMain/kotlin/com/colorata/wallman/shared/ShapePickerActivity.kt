package com.colorata.wallman.shared

import androidx.compose.runtime.Composable
import com.colorata.wallman.core.di.LocalGraph
import com.colorata.wallman.widget.ui.ShapePickerScreen

class ShapePickerActivity: GraphActivity() {
    @Composable
    override fun Content() {
        val graph = LocalGraph.current
        with(graph.widgetModule) {
            ShapePickerScreen()
        }
    }
}