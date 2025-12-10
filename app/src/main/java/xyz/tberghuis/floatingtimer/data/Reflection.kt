package xyz.tberghuis.floatingtimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflection_table")
data class Reflection(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val date: Long, // Epoch millis
  val wentWell: String,
  val challenges: String,
  val energyLevel: Int, // 1-5
  val prioritiesTomorrow: String,
  val notes: String
)
