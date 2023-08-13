package com.colorata.wallman.wallpapers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.simplifiedLocaleOf
import com.colorata.wallman.core.ui.DarkUIMode
import com.colorata.wallman.core.ui.LightUIMode
import com.colorata.wallman.core.ui.components.FontVariables
import com.colorata.wallman.core.ui.components.Text
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.spacing

@Composable
fun BigChip(
    onClick: () -> Unit,
    selected: Boolean,
    text: Polyglot,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val background = animateColorAsState(
        if (selected) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.surfaceVariant,
        label = "Background"
    )
    val roundness = animateIntAsState(
        targetValue = if (selected) 20 else 50, label = "Roundness",
        animationSpec = MaterialTheme.animation.emphasizedDecelerate()
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .graphicsLayer {
                shape = RoundedCornerShape(roundness.value)
                clip = true
            }
            .drawBehind {
                drawRect(background.value)
            }
            .clickable(enabled) {
                onClick()
            }
    ) {
        Text(
            text = rememberString(string = text),
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.large),
            fontVariables = FontVariables(
                weight = animateFloatAsState(
                    targetValue = if (selected) 900f else 400f, label = "Weight",
                    animationSpec = MaterialTheme.animation.emphasizedDecelerate()
                ).value
            ),
            color = animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "Color",
                animationSpec = MaterialTheme.animation.emphasizedDecelerate()
            ).value
        )
    }
}

private class SelectProvider: PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

@Preview(
    uiMode = LightUIMode
)
@Preview(
    uiMode = DarkUIMode
)
@Composable
private fun BigChipPreview(@PreviewParameter(SelectProvider::class) selected: Boolean) {
    WallManPreviewTheme {
        BigChip(
            onClick = {  },
            selected = selected,
            text = remember { simplifiedLocaleOf("Select") },
            Modifier.width(200.dp)
        )
    }
}