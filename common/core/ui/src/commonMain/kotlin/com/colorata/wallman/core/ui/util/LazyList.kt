package com.colorata.wallman.core.ui.util

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset

fun LazyStaggeredGridScope.fullLineItem(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyStaggeredGridItemScope.() -> Unit
) {
    item(
        key = key,
        contentType = contentType,
        span = StaggeredGridItemSpan.FullLine,
        content = content
    )
}