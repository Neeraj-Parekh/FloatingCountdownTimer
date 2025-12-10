package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.RingtoneType
import xyz.tberghuis.floatingtimer.data.preferencesRepository

import xyz.tberghuis.floatingtimer.service.audio.CustomSoundManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import java.io.File
import androidx.compose.runtime.collectAsState

class RingtoneScreenVm(private val application: Application) : AndroidViewModel(application) {
  private val prefRepo = application.preferencesRepository
  val ringtonePreviewVmc = RingtonePreviewVmc(application, viewModelScope)
  val currentRingtoneVmc =
    CurrentRingtoneVmc(prefRepo.alarmRingtoneUriFlow, prefRepo.customSoundNameFlow, viewModelScope, application)

  val systemDefaultAlarmVmc = SystemDefaultAlarmVmc(application, viewModelScope)

  val alarmList = RingtoneListVmc(application, RingtoneType.ALARM, viewModelScope)
  val ringtoneList = RingtoneListVmc(application, RingtoneType.RINGTONE, viewModelScope)
  val notificationList = RingtoneListVmc(application, RingtoneType.NOTIFICATION, viewModelScope)
  
  // Custom Sounds
  val customSoundManager = CustomSoundManager(application)
  var customSounds by mutableStateOf(customSoundManager.getCustomSounds())
    private set
    
  fun refreshCustomSounds() {
      customSounds = customSoundManager.getCustomSounds()
  }
  
  fun addCustomSound(uri: android.net.Uri) {
      val name = "custom_${System.currentTimeMillis()}" // simple naming
      customSoundManager.saveCustomSound(uri, name)
      refreshCustomSounds()
  }
  
  fun deleteCustomSound(soundFile: File) {
      customSoundManager.deleteCustomSound(soundFile.name)
      refreshCustomSounds()
  }

  // Audio Looping
  val audioLoopingFlow = prefRepo.audioLoopingFlow
  
  fun setAudioLooping(loop: Boolean) {
      viewModelScope.launch {
          prefRepo.updateAudioLooping(loop)
      }
  }

  fun setRingtone(uri: String) {
    viewModelScope.launch {
      prefRepo.updateCustomSoundName(null)
      prefRepo.updateAlarmRingtoneUri(uri)
    }
  }
  
  fun setCustomSound(name: String) {
    viewModelScope.launch {
      prefRepo.updateCustomSoundName(name)
    }
  }

  override fun onCleared() {
    ringtonePreviewVmc.onCleared()
    super.onCleared()
  }
}