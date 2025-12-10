package xyz.tberghuis.floatingtimer.data

import android.app.Application
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {
    val allSessions: Flow<List<SessionLog>> = sessionDao.getAllSessions()

    suspend fun logSession(session: SessionLog) {
        sessionDao.insertSession(session)
    }

    companion object {
        @Volatile
        private var instance: SessionRepository? = null

        fun getInstance(application: Application): SessionRepository {
            return instance ?: synchronized(this) {
                instance ?: SessionRepository(
                    AppDatabase.getInstance(application).sessionDao()
                ).also { instance = it }
            }
        }
    }
}
