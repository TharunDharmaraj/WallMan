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
import com.colorata.wallman.settings.overview.viewmodel.AboutViewModel

@Composable
fun AboutItem(item: AboutViewModel.AboutItem, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {
                item.onClick()
            }
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Icon(
                imageVector = item.icon, contentDescription = rememberString(string = item.name)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                androidx.compose.material3.Text(
                    text = rememberString(string = item.name),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                androidx.compose.material3.Text(
                    text = rememberString(string = item.description),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }
    }
}