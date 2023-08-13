package com.colorata.wallman.settings.overview.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.settings.overview.viewmodel.SettingsViewModel

@Composable
fun SettingsItem(
    item: SettingsViewModel.SettingsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                vertical = MaterialTheme.spacing.medium,
                horizontal = MaterialTheme.spacing.large
            ),
        verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
                imageVector = item.icon, contentDescription = rememberString(string = item.name)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                androidx.compose.material3.Text(
                    text = rememberString(string = item.name),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                androidx.compose.material3.Text(
                    text = rememberString(string = item.description),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}