package xyz.tberghuis.floatingtimer.data

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.logd

class TaskRepository(application: Application) {
    private val taskDao: TaskDao
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        val db = AppDatabase.getInstance(application)
        taskDao = db.taskDao()
    }

    // Exposed Flow for UI
    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
    val inboxTasks: Flow<List<Task>> = taskDao.getInboxTasks()

    fun getTasksByArea(area: String): Flow<List<Task>> = taskDao.getTasksByArea(area)

    private val _currentTaskIndex = MutableStateFlow(0)
    val currentTaskIndex = _currentTaskIndex.asStateFlow()

    fun addTask(name: String, durationSeconds: Int, color: Int, isInbox: Boolean = false, area: String = "General", category: String = "Default") {
        scope.launch {
            val newTask = Task(
                name = name,
                durationSeconds = durationSeconds,
                color = color,
                isInbox = isInbox,
                area = area,
                category = category,
                sortOrder = (System.currentTimeMillis() / 1000).toInt() // Simple sort by time
            )
            taskDao.insertTask(newTask)
        }
    }

    fun removeTask(task: Task) {
        scope.launch {
            taskDao.deleteTask(task)
            // Adjust index if needed logic logic might need to be reactive now, 
            // lets keep it simple for now as the List updates automatically via Flow
        }
    }

    fun updateTask(task: Task) {
        scope.launch {
            taskDao.updateTask(task)
        }
    }
    
    suspend fun moveNext() {
        val taskList = tasks.first()
        if (taskList.isNotEmpty()) {
            _currentTaskIndex.value = (_currentTaskIndex.value + 1) % taskList.size
            logd("TaskRepository moveNext index: ${_currentTaskIndex.value}")
        }
    }
    
    suspend fun getCurrentTask(): Task? {
        val taskList = tasks.first()
        if (taskList.isEmpty()) return null
        
        if (_currentTaskIndex.value !in taskList.indices) {
            _currentTaskIndex.value = 0
        }
        return taskList[_currentTaskIndex.value]
    }

    companion object {
        @Volatile
        private var instance: TaskRepository? = null
        
        fun getInstance(application: Application): TaskRepository {
            return instance ?: synchronized(this) {
                instance ?: TaskRepository(application).also { instance = it }
            }
        }
    }
}
