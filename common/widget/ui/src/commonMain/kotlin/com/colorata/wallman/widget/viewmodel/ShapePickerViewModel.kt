package com.colorata.wallman.widget.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.module.IntentHandler
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.widget.api.EverydayWidgetRepository
import com.colorata.wallman.widget.api.ShapeConfiguration
import com.colorata.wallman.widget.api.WidgetModule
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun WidgetModule.ShapePickerViewModel() =
    ShapePickerViewModel(widgetRepository, intentHandler, logger)

class ShapePickerViewModel(
    private val repository: EverydayWidgetRepository,
    private val intentHandler: IntentHandler,
    private val logger: Logger
) : ViewModel() {

    val state by lazyMolecule {
        return@lazyMolecule ScreenState(
            repository.shapes.toImmutableList(),
            repository.currentShape() ?: repository.shapes[0]
        ) { event ->
            when (event) {
                is ScreenEvent.ClickOnShape -> {
                    viewModelScope.launchIO({ logger.throwable(it) }) {
                        repository.updateShape(event.shape)
                        intentHandler.exit()
                    }
                }
            }
        }
    }

    @Immutable
    data class ScreenState(
        val shapes: ImmutableList<ShapeConfiguration>,
        val selectedShape: ShapeConfiguration,
        val onEvent: (ScreenEvent) -> Unit
    )

    @Immutable
    sealed interface ScreenEvent {
        data class ClickOnShape(val shape: ShapeConfiguration) : ScreenEvent
    }
}