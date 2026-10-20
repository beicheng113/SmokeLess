package com.example.smokeless.util

import java.util.concurrent.TimeUnit

/**
 * Fixed, public health-recovery milestones. These are bundled with the app and
 * never fetched from the network.
 */
data class HealthMilestone(
    val elapsedMillis: Long,
    val label: String,
    val description: String,
)

data class MilestoneState(
    val reached: HealthMilestone?,
    val next: HealthMilestone?,
)

object HealthMilestones {

    private fun minutes(value: Long): Long = TimeUnit.MINUTES.toMillis(value)
    private fun hours(value: Long): Long = TimeUnit.HOURS.toMillis(value)
    private fun days(value: Long): Long = TimeUnit.DAYS.toMillis(value)

    val all: List<HealthMilestone> = listOf(
        HealthMilestone(
            elapsedMillis = minutes(20L),
            label = "20 分钟",
            description = "心率和血压开始回落",
        ),
        HealthMilestone(
            elapsedMillis = hours(8L),
            label = "8 小时",
            description = "血液一氧化碳水平降至正常",
        ),
        HealthMilestone(
            elapsedMillis = hours(24L),
            label = "24 小时",
            description = "尼古丁基本代谢清除，呼吸功能初步改善",
        ),
        HealthMilestone(
            elapsedMillis = days(14L),
            label = "2 周",
            description = "牙龈血液循环接近不吸烟者水平",
        ),
        HealthMilestone(
            elapsedMillis = days(30L),
            label = "1 个月",
            description = "口臭改善，睡眠质量提升",
        ),
        HealthMilestone(
            elapsedMillis = days(90L),
            label = "3 个月",
            description = "肺功能显著提升，呼吸道纤毛开始修复",
        ),
        HealthMilestone(
            elapsedMillis = days(365L),
            label = "1 年",
            description = "冠心病风险降至持续吸烟者的一半",
        ),
        HealthMilestone(
            elapsedMillis = days(365L * 5L),
            label = "5 年",
            description = "脑卒中风险趋近非吸烟者",
        ),
    )

    /** Only the latest reached milestone and the next one are needed by the UI. */
    fun stateFor(elapsedMillis: Long): MilestoneState {
        val safeElapsed = elapsedMillis.coerceAtLeast(0L)
        val reached = all.lastOrNull { safeElapsed >= it.elapsedMillis }
        val next = all.firstOrNull { safeElapsed < it.elapsedMillis }
        return MilestoneState(reached = reached, next = next)
    }
}
