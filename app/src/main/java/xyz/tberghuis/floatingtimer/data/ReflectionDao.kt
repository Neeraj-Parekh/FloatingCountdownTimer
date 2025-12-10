package xyz.tberghuis.floatingtimer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {
  @Query("SELECT * FROM reflection_table ORDER BY date DESC")
  fun getAllReflections(): Flow<List<Reflection>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReflection(reflection: Reflection)
  
  @Query("DELETE FROM reflection_table")
  suspend fun deleteAll()
}
