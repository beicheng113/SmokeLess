package com.example.smokeless.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmokeDao {

    @Query("SELECT * FROM smoke_events ORDER BY timestampMillis DESC, id DESC")
    fun observeEvents(): Flow<List<SmokeEvent>>

    @Query("SELECT * FROM quit_state WHERE id = 0 LIMIT 1")
    fun observeQuitState(): Flow<QuitState?>

    @Query("SELECT * FROM quit_state WHERE id = 0 LIMIT 1")
    suspend fun getQuitState(): QuitState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuitState(state: QuitState)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuitStateIfAbsent(state: QuitState)

    @Insert
    suspend fun insertEvent(event: SmokeEvent): Long

    @Query("DELETE FROM smoke_events WHERE id = :id")
    suspend fun deleteEvent(id: Long)
}
