package com.example.smokeless.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One recorded cigarette. The dashboard is the only place that writes these,
 * and it does so silently after a 1.5 second hold.
 */
@Entity(tableName = "smoke_events")
data class SmokeEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestampMillis: Long,
)
