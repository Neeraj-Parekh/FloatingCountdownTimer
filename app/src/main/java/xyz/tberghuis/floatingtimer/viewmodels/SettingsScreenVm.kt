package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.preferencesRepository

class SettingsScreenVm(
  application: Application,
) :
  AndroidViewModel(application) {
  private val prefs = application.preferencesRepository

  val currentRingtoneVmc =
    CurrentRingtoneVmc(prefs.alarmRingtoneUriFlow, prefs.customSoundNameFlow, viewModelScope, application)

  val loopingFlow = prefs.loopingFlow
  val haloColourFlow = prefs.haloColourFlow
  val haloColourPurchasedFlow = prefs.haloColourPurchasedFlow
  val autoStartFlow = prefs.autoStartFlow

  fun updateLooping(value: Boolean) {
    viewModelScope.launch {
      prefs.updateLooping(value)
    }
  }

  fun updateAutoStart(value: Boolean) {
    viewModelScope.launch {
      prefs.updateAutoStart(value)
    }
  }
  
  val flashEnabledFlow = prefs.flashEnabledFlow
  val flashColorFlow = prefs.flashColorFlow
  val visualStyleFlow = prefs.visualStyleFlow

  fun updateFlashEnabled(value: Boolean) {
    viewModelScope.launch {
      prefs.updateFlashEnabled(value)
    }
  }
  
  fun updateVisualStyle(style: xyz.tberghuis.floatingtimer.data.TimerVisualStyle) {
    viewModelScope.launch {
        prefs.updateVisualStyle(style)
    }
  }

  val secondaryColorFlow = prefs.secondaryColorFlow

  fun updateSecondaryColor(color: androidx.compose.ui.graphics.Color) {
    viewModelScope.launch {
        prefs.updateSecondaryColor(color)
    }
  }

  val aodEnabledFlow = prefs.aodEnabledFlow
  val themeModeFlow = prefs.themeModeFlow

  fun updateAodEnabled(enabled: Boolean) {
    viewModelScope.launch {
        prefs.updateAodEnabled(enabled)
    }
  }

  fun updateThemeMode(mode: String) {
    viewModelScope.launch {
        prefs.updateThemeMode(mode)
    }
  }

  val audioMaskingEnabledFlow = prefs.audioMaskingEnabledFlow
  
  fun updateAudioMaskingEnabled(enabled: Boolean) {
      viewModelScope.launch {
          prefs.updateAudioMaskingEnabled(enabled)
      }
  }
}