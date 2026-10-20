package com.example.smokeless.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.ceil

/**
 * All time math for the app. Everything is local-device time; nothing is sent
 * anywhere. The dashboard recomputes this every second from Room state.
 */
object TimeUtils {

    private val clockFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("M月d日")
    private val longDateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    private val todayFormatter = DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.SIMPLIFIED_CHINESE)

    data class DurationParts(
        val days: Long,
        val hours: Long,
        val minutes: Long,
    )

    fun elapsedMillis(startMillis: Long, nowMillis: Long): Long =
        (nowMillis - startMillis).coerceAtLeast(0L)

    fun durationParts(startMillis: Long, nowMillis: Long): DurationParts {
        val totalMinutes = elapsedMillis(startMillis, nowMillis) / 60_000L
        return DurationParts(
            days = totalMinutes / (24L * 60L),
            hours = (totalMinutes / 60L) % 24L,
            minutes = totalMinutes % 60L,
        )
    }

    /** A compact remaining time, rounded up so a milestone never says "0 分钟". */
    fun formatRemaining(remainingMillis: Long): String {
        val safeMillis = remainingMillis.coerceAtLeast(0L)
        val totalMinutes = ceil(safeMillis / 60_000.0).toLong()
        if (totalMinutes <= 0L) return "现在"
        if (totalMinutes < 60L) return "$totalMinutes 分钟"

        if (totalMinutes < 24L * 60L) {
            val hours = totalMinutes / 60L
            val minutes = totalMinutes % 60L
            return if (minutes == 0L) "$hours 小时" else "$hours 小时 $minutes 分钟"
        }

        val days = totalMinutes / (24L * 60L)
        val hours = (totalMinutes % (24L * 60L)) / 60L
        return if (hours == 0L) "$days 天" else "$days 天 $hours 小时"
    }

    fun formatClock(epochMillis: Long): String =
        clockFormatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))

    fun todayLabel(nowMillis: Long): String {
        val date = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        return todayFormatter.format(date)
    }

    fun formatStartMoment(epochMillis: Long): String {
        val dateTime = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
        return "${shortDateFormatter.format(dateTime)} ${clockFormatter.format(dateTime)}"
    }

    fun dayLabel(date: LocalDate, today: LocalDate): String = when (date) {
        today -> "今天"
        today.minusDays(1) -> "昨天"
        else -> if (date.year == today.year) {
            shortDateFormatter.format(date)
        } else {
            longDateFormatter.format(date)
        }
    }

    fun weekStartMillis(nowMillis: Long): Long {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.atStartOfDay(zone).toInstant().toEpochMilli()
    }

    fun previousWeekStartMillis(nowMillis: Long): Long {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val lastMonday = today
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .minusWeeks(1)
        return lastMonday.atStartOfDay(zone).toInstant().toEpochMilli()
    }
}
