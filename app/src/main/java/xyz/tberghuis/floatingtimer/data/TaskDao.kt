package xyz.tberghuis.floatingtimer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isInbox = 1 ORDER BY id DESC")
    fun getInboxTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE area = :area AND isInbox = 0 ORDER BY sortOrder ASC")
    fun getTasksByArea(area: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}
