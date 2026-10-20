package com.example.smokeless.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * A soft, mostly transparent background. Two ambient circles add depth without
 * turning the app into a dashboard full of decoration.
 */
@Composable
fun SoftBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    colors.background,
                    colors.surfaceVariant.copy(alpha = 0.20f),
                    colors.background,
                ),
            ),
        ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val topCenter = Offset(size.width * 0.88f, size.height * 0.10f)
            val topRadius = size.minDimension * 0.48f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.primary.copy(alpha = 0.14f), Color.Transparent),
                    center = topCenter,
                    radius = topRadius,
                ),
                radius = topRadius,
                center = topCenter,
            )

            val bottomCenter = Offset(size.width * 0.10f, size.height * 0.78f)
            val bottomRadius = size.minDimension * 0.42f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors.secondary.copy(alpha = 0.10f), Color.Transparent),
                    center = bottomCenter,
                    radius = bottomRadius,
                ),
                radius = bottomRadius,
                center = bottomCenter,
            )
        }
        content()
    }
}
