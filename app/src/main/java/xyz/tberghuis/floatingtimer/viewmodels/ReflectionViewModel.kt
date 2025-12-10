package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.Reflection
import xyz.tberghuis.floatingtimer.data.ReflectionRepository

class ReflectionViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = ReflectionRepository(application)
  
  val allReflections = repository.getAllReflections()

  fun addReflection(
    wentWell: String, 
    challenges: String, 
    energyLevel: Int, 
    priorities: String, 
    notes: String
  ) {
    viewModelScope.launch {
      val reflection = Reflection(
          date = System.currentTimeMillis(),
          wentWell = wentWell,
          challenges = challenges,
          energyLevel = energyLevel,
          prioritiesTomorrow = priorities,
          notes = notes
      )
      repository.insertReflection(reflection)
    }
  }
}
