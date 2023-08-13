package com.colorata.wallman.core.ui.components

import androidx.annotation.FloatRange
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.colorata.wallman.core.ui.R

@OptIn(ExperimentalTextApi::class)
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    fontVariables: FontVariables = FontVariables(),
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val fontFamily = remember(fontVariables) {
        FontFamily(
            Font(
                R.font.robot_flex,
                variationSettings = fontVariables.createVariationSettings()
            )
        )
    }
    androidx.compose.material3.Text(
        text,
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        style
    )
}

data class FontVariables(
    @FloatRange(from = 100.0, to = 1000.0) val weight: Float = 400f,
    @FloatRange(from = 25.0, to = 151.0) val width: Float = 100f,
    @FloatRange(from = -10.0, to = 0.0) val slant: Float = 0f,
    @FloatRange(from = 649.0, to = 854.0) val ascenderHeight: Float = 750f,
    @FloatRange(from = 323.0, to = 603.0) val counterWidth: Float = 468f,
    @FloatRange(from = -305.0, to = -98.0) val descenderDepth: Float = -203f,
    @FloatRange(from = 560.0, to = 788.0) val figureHeight: Float = 738f,
    @FloatRange(from = -200.0, to = 150.0) val grade: Float = 0f,
    @FloatRange(from = 416.0, to = 570.0) val lowercaseHeight: Float = 514f,
    @FloatRange(from = 25.0, to = 135.0) val thinStroke: Float = 79f,
    @FloatRange(from = 528.0, to = 760.0) val uppercaseHeight: Float = 712f
)

fun FontVariables.createVariationSettings() = FontVariation.Settings(
    FontVariation.Setting("wght", weight),
    FontVariation.Setting("wdth", width),
    FontVariation.Setting("slnt", slant),
    FontVariation.Setting("GRAD", grade),
    FontVariation.Setting("XTRA", counterWidth),
    FontVariation.Setting("YOPQ", thinStroke),
    FontVariation.Setting("YTAS", ascenderHeight),
    FontVariation.Setting("YTDE", descenderDepth),
    FontVariation.Setting("YTFI", figureHeight),
    FontVariation.Setting("YTLC", lowercaseHeight),
    FontVariation.Setting("YTUC", uppercaseHeight),
)