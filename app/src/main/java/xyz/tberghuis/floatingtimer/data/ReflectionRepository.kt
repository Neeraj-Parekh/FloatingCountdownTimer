package xyz.tberghuis.floatingtimer.data

import android.app.Application
import kotlinx.coroutines.flow.Flow

class ReflectionRepository(application: Application) {
  private val reflectionDao: ReflectionDao

  init {
    val db = AppDatabase.getInstance(application)
    reflectionDao = db.reflectionDao()
  }

  fun getAllReflections(): Flow<List<Reflection>> {
    return reflectionDao.getAllReflections()
  }

  suspend fun insertReflection(reflection: Reflection) {
    reflectionDao.insertReflection(reflection)
  }
}
