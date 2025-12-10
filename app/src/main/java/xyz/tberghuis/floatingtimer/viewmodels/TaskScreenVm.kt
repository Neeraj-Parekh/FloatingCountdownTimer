package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import xyz.tberghuis.floatingtimer.data.Task
import xyz.tberghuis.floatingtimer.data.TaskRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TaskScreenVm(application: Application) : AndroidViewModel(application) {
  private val taskRepository = TaskRepository.getInstance(application)
  
  // Tab state: "Inbox", "Work", "Personal"
  val currentTab = MutableStateFlow("Inbox")
  
  @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
  val tasks = currentTab.flatMapLatest { tab ->
      when (tab) {
          "Inbox" -> taskRepository.inboxTasks
          else -> taskRepository.getTasksByArea(tab)
      }
  }

  fun addTask(name: String, durationSeconds: Int, color: Int, area: String, isInbox: Boolean) {
    taskRepository.addTask(name, durationSeconds, color, isInbox, area)
  }
  
  fun deleteTask(task: Task) {
    taskRepository.removeTask(task)
  }
}
