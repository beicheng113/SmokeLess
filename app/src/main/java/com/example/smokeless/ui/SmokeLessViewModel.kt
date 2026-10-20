package com.example.smokeless.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smokeless.data.SmokeEvent
import com.example.smokeless.data.SmokeLessDatabase
import com.example.smokeless.data.SmokeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SmokeUiState(
    val startMillis: Long = System.currentTimeMillis(),
    val nowMillis: Long = System.currentTimeMillis(),
    val events: List<SmokeEvent> = emptyList(),
)

class SmokeLessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SmokeRepository(
        SmokeLessDatabase.getInstance(application),
    )

    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1_000L)
        }
    }

    val uiState: StateFlow<SmokeUiState> = combine(
        repository.quitStartMillis,
        repository.events,
        ticker,
    ) { startMillis, events, nowMillis ->
        SmokeUiState(
            startMillis = startMillis ?: nowMillis,
            nowMillis = nowMillis,
            events = events,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SmokeUiState(),
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun recordSmoke() {
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            repository.recordSmoke(now)
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            repository.deleteEvent(id)
        }
    }
}
