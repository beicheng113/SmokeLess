package com.example.smokeless.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The start of the current smoke-free stretch. Only one row exists (id = 0).
 * It is moved to "now" whenever a cigarette is recorded.
 */
@Entity(tableName = "quit_state")
data class QuitState(
    @PrimaryKey
    val id: Int = 0,
    val startMillis: Long,
)
