package com.example.smokeless.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smokeless.data.SmokeEvent
import com.example.smokeless.ui.components.SoftBackground
import com.example.smokeless.util.HistoryUtils
import com.example.smokeless.util.TimeUtils

@Composable
fun LogScreen(
    events: List<SmokeEvent>,
    nowMillis: Long,
    onDelete: (Long) -> Unit,
) {
    val weekCounts = remember(events, nowMillis) {
        HistoryUtils.weekCounts(events, nowMillis)
    }
    val groups = remember(events, nowMillis) {
        HistoryUtils.groupByDay(events, nowMillis)
    }

    SoftBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "记录",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "长按一条记录可删除",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f),
                    tonalElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "仅本机",
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WeekStatCard(
                    label = "本周",
                    count = weekCounts.thisWeek,
                    accent = true,
                    modifier = Modifier.weight(1f),
                )
                WeekStatCard(
                    label = "上周",
                    count = weekCounts.lastWeek,
                    accent = false,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (events.isEmpty()) {
                EmptyRecords(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    groups.forEach { group ->
                        item(key = "header-${group.label}") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
                                    tonalElevation = 0.dp,
                                ) {
                                    Text(
                                        text = group.label,
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        items(group.events, key = { it.id }) { event ->
                            RecordCard(
                                event = event,
                                onDelete = onDelete,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekStatCard(
    label: String,
    count: Int,
    accent: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)
    val startColor = if (accent) {
        colors.primaryContainer.copy(alpha = 0.92f)
    } else {
        colors.surfaceVariant.copy(alpha = 0.75f)
    }
    val endColor = colors.surface.copy(alpha = 0.94f)
    val contentColor = if (accent) colors.onPrimaryContainer else colors.onBackground

    Box(
        modifier = modifier
            .clip(shape)
            .background(Brush.linearGradient(listOf(startColor, endColor)))
            .border(1.dp, colors.outline.copy(alpha = 0.08f), shape),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
                .size(52.dp)
                .border(
                    width = 9.dp,
                    color = contentColor.copy(alpha = 0.045f),
                    shape = CircleShape,
                ),
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.60f),
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count.toString(),
                    fontSize = 31.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor,
                )
                Text(
                    text = " 支",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.62f),
                    modifier = Modifier.padding(bottom = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun RecordCard(
    event: SmokeEvent,
    onDelete: (Long) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.08f)),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(event.id) {
                    detectTapGestures(
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDelete(event.id)
                        },
                    )
                }
                .semantics {
                    onLongClick(label = "删除这条记录") {
                        onDelete(event.id)
                        true
                    }
                }
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 24.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.62f)),
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = TimeUtils.formatClock(event.timestampMillis),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = colors.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colors.onSurfaceVariant.copy(alpha = 0.22f)),
            )
        }
    }
}

@Composable
private fun EmptyRecords(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.08f))
                    .border(1.dp, colors.primary.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = colors.primary.copy(alpha = 0.58f),
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "还没有记录。",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "这里会留下你记录的每一次。",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}
