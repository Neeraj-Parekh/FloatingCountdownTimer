package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.map
import xyz.tberghuis.floatingtimer.data.SessionRepository
import java.util.Calendar

class StatsScreenVm(application: Application) : AndroidViewModel(application) {
    private val sessionRepository = SessionRepository.getInstance(application)

    // Simplified stats for now: Total time today, list of recent sessions
    val sessions = sessionRepository.allSessions

    val todayTotalSeconds = sessions.map { list ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        list.filter { it.startTime >= startOfDay }.sumOf { it.durationSeconds }
    }
}
