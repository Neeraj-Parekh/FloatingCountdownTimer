package xyz.tberghuis.floatingtimer.data

import android.app.Application
import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.tberghuis.floatingtimer.DB_FILENAME

@Database(
  entities = [
      SavedCountdown::class,
      SavedStopwatch::class,
      Reflection::class,
      Task::class,
      SessionLog::class
  ],
  version = 8,
  exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun savedCountdownDao(): SavedCountdownDao
  abstract fun savedStopwatchDao(): SavedStopwatchDao
  abstract fun reflectionDao(): ReflectionDao
  abstract fun taskDao(): TaskDao
  abstract fun sessionDao(): SessionDao

  companion object {
    @Volatile
    private var instance: AppDatabase? = null
    fun getInstance(application: Application) =
      instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
          application,
          AppDatabase::class.java,
          DB_FILENAME
        )
          .fallbackToDestructiveMigration()
          .build()
          .also { instance = it }
      }
  }
}

val Context.appDatabase: AppDatabase
  get() = AppDatabase.getInstance(this.applicationContext as Application)