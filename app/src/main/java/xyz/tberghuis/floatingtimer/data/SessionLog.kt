package xyz.tberghuis.floatingtimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_log")
data class SessionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int? = null, // Nullable if generic timer
    val taskName: String? = null, // Store name in case task is deleted
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Int
)
