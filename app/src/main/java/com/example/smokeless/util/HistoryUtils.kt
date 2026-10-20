package com.example.smokeless.util

import com.example.smokeless.data.SmokeEvent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DayEvents(
    val label: String,
    val events: List<SmokeEvent>,
)

data class WeekCounts(
    val thisWeek: Int,
    val lastWeek: Int,
)

object HistoryUtils {

    fun weekCounts(events: List<SmokeEvent>, nowMillis: Long): WeekCounts {
        val thisWeekStart = TimeUtils.weekStartMillis(nowMillis)
        val lastWeekStart = TimeUtils.previousWeekStartMillis(nowMillis)
        val thisWeek = events.count { it.timestampMillis >= thisWeekStart }
        val lastWeek = events.count { it.timestampMillis in lastWeekStart until thisWeekStart }
        return WeekCounts(thisWeek = thisWeek, lastWeek = lastWeek)
    }

    fun groupByDay(events: List<SmokeEvent>, nowMillis: Long): List<DayEvents> {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val grouped = LinkedHashMap<LocalDate, MutableList<SmokeEvent>>()

        events
            .sortedWith(compareByDescending<SmokeEvent> { it.timestampMillis }.thenByDescending { it.id })
            .forEach { event ->
                val date = Instant.ofEpochMilli(event.timestampMillis).atZone(zone).toLocalDate()
                grouped.getOrPut(date) { mutableListOf() }.add(event)
            }

        return grouped.map { (date, dayEvents) ->
            DayEvents(
                label = TimeUtils.dayLabel(date, today),
                events = dayEvents,
            )
        }
    }
}
