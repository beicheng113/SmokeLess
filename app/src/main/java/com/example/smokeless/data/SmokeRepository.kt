package com.example.smokeless.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SmokeRepository(
    private val database: SmokeLessDatabase,
) {
    private val dao = database.smokeDao()

    val quitStartMillis: Flow<Long?> = dao.observeQuitState().map { state ->
        state?.startMillis
    }

    val events: Flow<List<SmokeEvent>> = dao.observeEvents()

    suspend fun ensureInitialized(nowMillis: Long = System.currentTimeMillis()) {
        dao.insertQuitStateIfAbsent(QuitState(startMillis = nowMillis))
    }

    suspend fun recordSmoke(nowMillis: Long = System.currentTimeMillis()) {
        database.withTransaction {
            dao.insertEvent(SmokeEvent(timestampMillis = nowMillis))
            dao.upsertQuitState(QuitState(startMillis = nowMillis))
        }
    }

    suspend fun deleteEvent(id: Long) {
        dao.deleteEvent(id)
    }
}
