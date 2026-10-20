package com.example.smokeless.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smokeless.util.HealthMilestone
import com.example.smokeless.util.HealthMilestones
import com.example.smokeless.util.TimeUtils

/**
 * Shows at most two milestones: the latest one reached and the next one.
 * No full table and no chart.
 */
@Composable
fun MilestoneTimeline(
    elapsedMillis: Long,
    modifier: Modifier = Modifier,
) {
    val state = remember(elapsedMillis) { HealthMilestones.stateFor(elapsedMillis) }
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "身体正在发生什么",
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "当前 · 下一段",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant.copy(alpha = 0.55f),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = colors.surface.copy(alpha = 0.72f),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.09f)),
            tonalElevation = 0.dp,
        ) {
            Column(modifier = Modifier.padding(horizontal = 17.dp, vertical = 16.dp)) {
                state.reached?.let { milestone ->
                    MilestoneRow(
                        milestone = milestone,
                        reached = true,
                        elapsedMillis = elapsedMillis,
                    )
                    if (state.next != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = colors.outline.copy(alpha = 0.10f),
                        )
                    }
                }

                state.next?.let { milestone ->
                    MilestoneRow(
                        milestone = milestone,
                        reached = false,
                        elapsedMillis = elapsedMillis,
                    )
                }

                if (state.reached != null && state.next == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "这里列出的里程碑都已经走过。",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneRow(
    milestone: HealthMilestone,
    reached: Boolean,
    elapsedMillis: Long,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(24.dp)
                .clip(CircleShape)
                .then(
                    if (reached) {
                        Modifier.background(colors.primary)
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = colors.outline.copy(alpha = 0.45f),
                            shape = CircleShape,
                        )
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (reached) {
                Text(
                    text = "✓",
                    color = colors.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.72f)),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = milestone.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = CircleShape,
                    color = if (reached) {
                        colors.primary.copy(alpha = 0.10f)
                    } else {
                        colors.secondary.copy(alpha = 0.10f)
                    },
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = if (reached) {
                            "已到达"
                        } else {
                            "还有 " + TimeUtils.formatRemaining(milestone.elapsedMillis - elapsedMillis)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (reached) colors.primary else colors.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}
