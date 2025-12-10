package xyz.tberghuis.floatingtimer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionLog)

    @Query("SELECT * FROM session_log ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionLog>>
    
    @Query("SELECT * FROM session_log WHERE startTime BETWEEN :start AND :end")
    fun getSessionsInRange(start: Long, end: Long): Flow<List<SessionLog>>
}
