package com.colorata.wallman.widget.impl

import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.widget.api.R
import com.colorata.wallman.widget.api.ShapeConfiguration

val shapes by lazy {
    listOf(
        ShapeConfiguration(
            R.drawable.ic_clever,
            Strings.clever
        ),
        ShapeConfiguration(
            R.drawable.ic_flower,
            Strings.flower
        ),
        ShapeConfiguration(
            R.drawable.ic_scallop,
            Strings.scallop
        ),
        ShapeConfiguration(
            R.drawable.ic_circle,
            Strings.circle
        ),
        ShapeConfiguration(
            R.drawable.ic_square,
            Strings.square
        )
    )
}