package com.example.smokeless.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smokeless.ui.SmokeUiState
import com.example.smokeless.ui.components.HoldToConfirmButton
import com.example.smokeless.ui.components.MilestoneTimeline
import com.example.smokeless.ui.components.SmokeLessMark
import com.example.smokeless.ui.components.SoftBackground
import com.example.smokeless.util.HealthMilestones
import com.example.smokeless.util.MilestoneState
import com.example.smokeless.util.TimeUtils

@Composable
fun DashboardScreen(
    uiState: SmokeUiState,
    onRecordSmoke: () -> Unit,
    onOpenSos: () -> Unit,
) {
    val elapsedMillis = TimeUtils.elapsedMillis(uiState.startMillis, uiState.nowMillis)
    val parts = TimeUtils.durationParts(uiState.startMillis, uiState.nowMillis)
    val milestoneState = HealthMilestones.stateFor(elapsedMillis)

    SoftBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 140.dp),
            ) {
                DashboardHeader(nowMillis = uiState.nowMillis)

                Spacer(modifier = Modifier.height(20.dp))

                HeroCard(
                    parts = parts,
                    startMillis = uiState.startMillis,
                    elapsedMillis = elapsedMillis,
                    milestoneState = milestoneState,
                )

                Spacer(modifier = Modifier.height(18.dp))

                QuickBreathCard(onClick = onOpenSos)

                Spacer(modifier = Modifier.height(22.dp))

                MilestoneTimeline(elapsedMillis = elapsedMillis)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 22.dp, bottom = 22.dp),
            ) {
                HoldToConfirmButton(
                    label = "我抽了一支",
                    onConfirmed = onRecordSmoke,
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(nowMillis: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SmokeLessMark(size = 46.dp)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = "SmokeLess",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "今天 · ${TimeUtils.todayLabel(nowMillis)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        LiveBadge()
    }
}

@Composable
private fun LiveBadge() {
    val transition = rememberInfiniteTransition(label = "live")
    val dotAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "liveAlpha",
    )

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha)),
            )
            Text(
                text = "进行中",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun HeroCard(
    parts: TimeUtils.DurationParts,
    startMillis: Long,
    elapsedMillis: Long,
    milestoneState: MilestoneState,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(30.dp)
    val next = milestoneState.next
    val previousMillis = milestoneState.reached?.elapsedMillis ?: 0L
    val rawProgress = if (next != null) {
        ((elapsedMillis - previousMillis).toFloat() / (next.elapsedMillis - previousMillis).toFloat())
            .coerceIn(0f, 1f)
    } else {
        1f
    }
    val progress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 800),
        label = "milestoneProgress",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        colors.primaryContainer.copy(alpha = 0.96f),
                        colors.secondaryContainer.copy(alpha = 0.60f),
                        colors.surface.copy(alpha = 0.92f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = colors.primary.copy(alpha = 0.18f),
                shape = shape,
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 42.dp, y = (-54).dp)
                .size(176.dp)
                .border(
                    width = 22.dp,
                    color = colors.onPrimaryContainer.copy(alpha = 0.045f),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-36).dp, y = 58.dp)
                .size(118.dp)
                .border(
                    width = 14.dp,
                    color = colors.onPrimaryContainer.copy(alpha = 0.035f),
                    shape = CircleShape,
                ),
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "无烟时长",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onPrimaryContainer.copy(alpha = 0.72f),
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "只存这台设备",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onPrimaryContainer.copy(alpha = 0.46f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                TimerSegment(value = parts.days, label = "天", modifier = Modifier.weight(1f))
                TimerSegment(value = parts.hours, label = "小时", modifier = Modifier.weight(1f))
                TimerSegment(value = parts.minutes, label = "分钟", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "从 ${TimeUtils.formatStartMoment(startMillis)} 开始",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onPrimaryContainer.copy(alpha = 0.58f),
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.onPrimaryContainer.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(14.dp))

            if (next != null) {
                Text(
                    text = "距下一段健康变化还有 " +
                        TimeUtils.formatRemaining(next.elapsedMillis - elapsedMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(9.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(colors.onPrimaryContainer.copy(alpha = 0.13f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .clip(CircleShape)
                            .background(colors.onPrimaryContainer.copy(alpha = 0.72f)),
                    )
                }
            } else {
                Text(
                    text = "这里列出的健康变化都已经走过。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun TimerSegment(
    value: Long,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = colors.surface.copy(alpha = 0.56f),
        border = BorderStroke(1.dp, colors.onPrimaryContainer.copy(alpha = 0.06f)),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value.toString(),
                fontSize = 34.sp,
                lineHeight = 39.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onPrimaryContainer,
                maxLines = 1,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onPrimaryContainer.copy(alpha = 0.58f),
            )
        }
    }
}

@Composable
private fun QuickBreathCard(onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(24.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.secondaryContainer.copy(alpha = 0.42f))
            .border(1.dp, colors.secondary.copy(alpha = 0.13f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(colors.surface.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = "烟瘾来了？",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
            )
            Text(
                text = "做一次 4-7-8 呼吸，约 1 分钟",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
        )
    }
}
