package com.colorata.wallman.categories.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.mutate
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.storagedImageBitmap
import com.colorata.wallman.core.ui.LightDarkPreview
import com.colorata.wallman.core.ui.shapes.FlowerShape
import com.colorata.wallman.core.ui.shapes.ScallopShape
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.firstPreviewRes
import com.colorata.wallman.wallpapers.walls
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: WallpaperCategory,
    wallpapers: ImmutableList<WallpaperI>,
    shapes: ImmutableList<Shape>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier, shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.small,
                Alignment.CenterHorizontally
            )
        ) {
            val roundedShape = MaterialTheme.shapes.large
            val circle = CircleShape
            val largeShapes = remember { persistentListOf(circle, roundedShape) }
            for (index in 0..2) {
                Image(
                    bitmap = storagedImageBitmap(wallpapers[index].firstPreviewRes()),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(shapes[index])
                        .height(80.dp)
                        .width(if (shapes[index] in largeShapes) 160.dp else 80.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        androidx.compose.material3.Text(
            text = rememberString(string = category.locale.name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.medium,
                bottom = MaterialTheme.spacing.small
            )
        )
        androidx.compose.material3.Text(
            text = rememberString(string = category.locale.description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
    }
}

internal fun generateShapesForCard(shapes: Shapes): ImmutableList<Shape> {
    val roundedShape = shapes.large
    val circle = CircleShape
    val largeShapes = persistentListOf(circle, roundedShape)
    return persistentListOf(
        circle, ScallopShape(), FlowerShape(), roundedShape
    ).shuffled().mutate {
        if (circle in take(3) && roundedShape in take(3)) remove(
            largeShapes.random()
        )
    }.toImmutableList()
}

@LightDarkPreview
@Composable
private fun CategorySample() {
    val shapes = MaterialTheme.shapes
    WallManPreviewTheme {
        CategoryCard(
            category = WallpaperCategory.Garden,
            walls,
            remember { generateShapesForCard(shapes) },
            {})
    }
}