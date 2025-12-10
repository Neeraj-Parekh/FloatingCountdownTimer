package xyz.tberghuis.floatingtimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color

import kotlinx.serialization.Serializable

@Entity(tableName = "tasks")
@Serializable
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val durationSeconds: Int,
    val color: Int = android.graphics.Color.WHITE, // Storing color as Int for Room compatibility
    
    // Phase 2 Fields
    val area: String = "General", // e.g., "Work", "Personal"
    val category: String = "Default", // e.g., "Deep Work", "Admin"
    val isInbox: Boolean = false, // True if added via Brain Dump
    val sortOrder: Int = 0 
)
