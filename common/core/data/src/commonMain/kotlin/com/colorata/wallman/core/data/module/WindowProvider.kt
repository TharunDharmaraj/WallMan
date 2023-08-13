package com.colorata.wallman.core.data.module

interface WindowProvider {
    fun isLayoutCompact(): Boolean

    fun isLayoutMedium(): Boolean

    fun isLayoutExpanded(): Boolean

    object NoopWindowProvider : WindowProvider {
        override fun isLayoutCompact(): Boolean = false

        override fun isLayoutMedium(): Boolean = false

        override fun isLayoutExpanded(): Boolean = false
    }
}