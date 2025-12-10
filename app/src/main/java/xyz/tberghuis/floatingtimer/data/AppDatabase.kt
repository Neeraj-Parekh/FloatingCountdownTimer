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
      SavedTimer::class,
      Reflection::class,
      Task::class,
      SessionLog::class
  ],
  version = 7,
  exportSchema = true,
  autoMigrations = [
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3),
    AutoMigration(from = 3, to = 4),
    AutoMigration(from = 4, to = 5),
    AutoMigration(from = 5, to = 6),
    AutoMigration(from = 6, to = 7)
  ]
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun savedTimerDao(): SavedTimerDao
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
          .build()
          .also { instance = it }
      }
  }
}

val Context.appDatabase: AppDatabase
  get() = AppDatabase.getInstance(this.applicationContext as Application)