package com.colorata.wallman.settings.mirror.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.sp
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.settings.mirror.api.Mirror

@Composable
fun MirrorCard(
    mirror: Mirror, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val animatedBackground by animateColorAsState(
        if (selected)
            MaterialTheme.colorScheme.primaryContainer else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "",
    )
    Column(
        modifier
            .clickable { onClick() }
            .drawBehind {
                drawRect(animatedBackground)
            }
            .padding(MaterialTheme.spacing.large)
            .fillMaxWidth()) {
        Text(text = rememberString(mirror.name), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = mirror.url,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}