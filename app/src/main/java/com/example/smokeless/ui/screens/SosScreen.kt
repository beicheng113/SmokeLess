package com.example.smokeless.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smokeless.util.CravingPhrases
import kotlinx.coroutines.delay

private enum class BreathPhase(
    val label: String,
    val durationMillis: Int,
    val seconds: Int,
) {
    Inhale(label = "吸气", durationMillis = 4_000, seconds = 4),
    Hold(label = "屏息", durationMillis = 7_000, seconds = 7),
    Exhale(label = "呼气", durationMillis = 8_000, seconds = 8),
}

/**
 * Full-screen breathing guide: 4s in, 7s hold, 8s out, three rounds.
 * The circle, the phase colour and the small round indicators all move with
 * the same rhythm. After round three, one phrase fades in.
 */
@Composable
fun SosScreen(onFinished: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val scale = remember { Animatable(0.72f) }
    val phrase = remember { CravingPhrases.random() }
    var phase by remember { mutableStateOf(BreathPhase.Inhale) }
    var round by remember { mutableIntStateOf(1) }
    var secondsLeft by remember { mutableIntStateOf(BreathPhase.Inhale.seconds) }
    var completed by remember { mutableStateOf(false) }
    var finishing by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme
    val phaseColor by animateColorAsState(
        targetValue = when (phase) {
            BreathPhase.Inhale -> colors.primary
            BreathPhase.Hold -> colors.secondary
            BreathPhase.Exhale -> colors.tertiary
        },
        animationSpec = tween(durationMillis = 650),
        label = "phaseColor",
    )

    val finishOnce: () -> Unit = {
        if (!finishing) {
            finishing = true
            onFinished()
        }
    }

    BackHandler {
        finishOnce()
    }

    LaunchedEffect(Unit) {
        repeat(3) { index ->
            round = index + 1

            phase = BreathPhase.Inhale
            scale.animateTo(
                targetValue = 1.20f,
                animationSpec = tween(
                    durationMillis = BreathPhase.Inhale.durationMillis,
                    easing = FastOutSlowInEasing,
                ),
            )

            phase = BreathPhase.Hold
            delay(BreathPhase.Hold.durationMillis.toLong())

            phase = BreathPhase.Exhale
            scale.animateTo(
                targetValue = 0.72f,
                animationSpec = tween(
                    durationMillis = BreathPhase.Exhale.durationMillis,
                    easing = FastOutSlowInEasing,
                ),
            )
        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        completed = true
    }

    LaunchedEffect(phase, round) {
        secondsLeft = phase.seconds
        for (second in phase.seconds downTo 1) {
            secondsLeft = second
            delay(1_000L)
        }
    }

    LaunchedEffect(completed) {
        if (completed) {
            delay(3_200L)
            finishOnce()
        }
    }

    val phaseProgress = 1f - (secondsLeft.toFloat() / phase.seconds.toFloat())
    val layerScale = scale.value
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colors.background,
            colors.surfaceVariant.copy(alpha = 0.36f),
            colors.background,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.42f)
            val radius = size.minDimension * 0.72f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(phaseColor.copy(alpha = 0.16f), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }

        if (!completed) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "烟瘾 SOS",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "4 · 7 · 8 呼吸",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        phaseColor.copy(alpha = 0.10f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )

                    CircularProgressIndicator(
                        progress = { phaseProgress.coerceIn(0f, 1f) },
                        modifier = Modifier.size(270.dp),
                        color = phaseColor.copy(alpha = 0.52f),
                        trackColor = phaseColor.copy(alpha = 0.08f),
                        strokeWidth = 2.dp,
                    )

                    Box(
                        modifier = Modifier
                            .size(226.dp)
                            .graphicsLayer {
                                scaleX = layerScale
                                scaleY = layerScale
                            }
                            .clip(CircleShape)
                            .background(phaseColor.copy(alpha = 0.09f))
                            .border(
                                width = 1.dp,
                                color = phaseColor.copy(alpha = 0.22f),
                                shape = CircleShape,
                            ),
                    )

                    Box(
                        modifier = Modifier
                            .size(164.dp)
                            .graphicsLayer {
                                scaleX = layerScale
                                scaleY = layerScale
                            }
                            .clip(CircleShape)
                            .background(phaseColor.copy(alpha = 0.15f))
                            .border(
                                width = 1.dp,
                                color = phaseColor.copy(alpha = 0.38f),
                                shape = CircleShape,
                            ),
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = phase.label,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = secondsLeft.toString(),
                            fontSize = 44.sp,
                            lineHeight = 48.sp,
                            fontWeight = FontWeight.Light,
                            color = colors.onBackground,
                        )
                        Text(
                            text = "第 $round / 3 轮",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "跟着圆呼吸，不用做别的。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(18.dp))

                RoundIndicators(
                    phaseColor = phaseColor,
                    currentRound = round,
                    completed = completed,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "4 秒吸气 · 7 秒屏息 · 8 秒呼气",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }

        AnimatedVisibility(
            visible = completed,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(initialScale = 0.96f),
            exit = fadeOut(),
        ) {
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = colors.surface.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.10f)),
                tonalElevation = 0.dp,
                shadowElevation = 10.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(colors.primary.copy(alpha = 0.11f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✓",
                            color = colors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = phrase,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "正在回到仪表盘…",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundIndicators(
    phaseColor: Color,
    currentRound: Int,
    completed: Boolean,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val isCurrent = !completed && index + 1 == currentRound
            val isDone = completed || index + 1 < currentRound
            val size = if (isCurrent) 9.dp else 6.dp
            val color = when {
                isDone || isCurrent -> phaseColor
                else -> colors.onSurfaceVariant.copy(alpha = 0.20f)
            }
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}
