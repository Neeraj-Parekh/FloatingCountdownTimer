package xyz.tberghuis.floatingtimer.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.coroutines.flow.first

@Serializable
data class BackupData(
    val tasks: List<Task>,
    val sessions: List<SessionLog>
)

class BackupManager(private val context: Context) {
    private val taskRepository = TaskRepository.getInstance(context.applicationContext as android.app.Application)
    private val sessionRepository = SessionRepository.getInstance(context.applicationContext as android.app.Application)

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportData(): File {
        val tasks = taskRepository.tasks.first()
        val sessions = sessionRepository.allSessions.first()
        val backupData = BackupData(tasks, sessions)
        
        val jsonString = json.encodeToString(backupData)
        val file = File(context.cacheDir, "floating_timer_backup.json")
        file.writeText(jsonString)
        return file
    }

    suspend fun importData(jsonString: String) {
        val backupData = json.decodeFromString<BackupData>(jsonString)
        // Clear existing? Or Append? Let's Append-ish or Upsert.
        // For simplicity, let's just add them. Duplicates might happen if ID logic isn't smart.
        // Task ID is auto-gen, so we should probably reset ID to 0 to let Room generate new IDs,
        // OR use ConflictStrategy.REPLACE.
        
        backupData.tasks.forEach { task ->
            // Insert as new or update. 
            // Since we can't easily upsert with current Repo, let's just add.
            // Ideally we'd map them to new instances.
            taskRepository.addTask(
                name = task.name,
                durationSeconds = task.durationSeconds,
                color = task.color,
                isInbox = task.isInbox,
                area = task.area,
                category = task.category
            )
        }
        
        backupData.sessions.forEach { session ->
             sessionRepository.logSession(session.copy(id = 0)) // Let Room gen ID
        }
    }
}
