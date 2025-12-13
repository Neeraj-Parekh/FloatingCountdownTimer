package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import xyz.tberghuis.floatingtimer.data.TaskRepository
import xyz.tberghuis.floatingtimer.service.boundFloatingServiceProvider
import xyz.tberghuis.floatingtimer.logd

class SharedVm(private val application: Application) : AndroidViewModel(application) {
  private val boundFloatingService = application.boundFloatingServiceProvider
  private val taskRepository = TaskRepository.getInstance(application)
  var showGrantOverlayDialog by mutableStateOf(false)
  
  fun cancelAllTimers() {
    viewModelScope.launch(IO) {
      try {
        boundFloatingService.provideService().overlayController.exitAll()
      } catch (e: Exception) {
        logd("Error canceling timers: ${e.message}")
      }
    }
  }

  fun saveTimerPositions() {
    viewModelScope.launch(IO) {
      try {
        boundFloatingService.provideService().overlayController.saveTimerPositions()
      } catch (e: Exception) {
        logd("Error saving positions: ${e.message}")
      }
    }
  }

  fun startTaskTimer() {
    viewModelScope.launch(IO) {
      try {
        val tasks = taskRepository.tasks.first()
        if (tasks.isEmpty()) {
          withContext(Main) {
            Toast.makeText(application, "No tasks found. Add tasks first in 'Manage Tasks'", Toast.LENGTH_LONG).show()
          }
          return@launch
        }
        boundFloatingService.provideService().overlayController.addCurrentTaskCountdown()
      } catch (e: Exception) {
        logd("Error starting task timer: ${e.message}")
        withContext(Main) {
          Toast.makeText(application, "Error starting task timer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}