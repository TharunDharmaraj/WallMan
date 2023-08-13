package com.colorata.wallman.widget.api

interface EverydayWidgetRepository {
    val shapes: List<ShapeConfiguration>

    fun currentShape(): ShapeConfiguration?

    fun initializeWorkManager()

    suspend fun updateShape(configuration: ShapeConfiguration)

    object NoopEverydayWidgetRepository : EverydayWidgetRepository {

        override val shapes: List<ShapeConfiguration> = emptyList()

        override fun currentShape(): ShapeConfiguration? {
            return null
        }

        override fun initializeWorkManager() {}
        override suspend fun updateShape(configuration: ShapeConfiguration) {}
    }
}