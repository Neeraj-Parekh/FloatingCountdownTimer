package xyz.tberghuis.floatingtimer.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.godaddy.android.colorpicker.HsvColor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.DEFAULT_HALO_COLOR
import xyz.tberghuis.floatingtimer.data.preferencesRepository
import xyz.tberghuis.floatingtimer.logd

class ColorSettingViewModel(application: Application, savedStateHandle: SavedStateHandle) :
  AndroidViewModel(application) {
  private val preferences = application.preferencesRepository
  var colorPickerColorState = mutableStateOf(HsvColor.from(DEFAULT_HALO_COLOR))

  val premiumVmc = PremiumVmc(application, viewModelScope)

  private val premiumFlow = application.preferencesRepository.haloColourPurchasedFlow

  var initialised by mutableStateOf(false)
  lateinit var settingsTimerPreviewVmc: SettingsTimerPreviewVmc

  // null == default color
  // if not null, set previousBackStackEntry color_result
  val timerType: String? = savedStateHandle["timerType"]

  var hexText by mutableStateOf("")

  init {
    logd("ColorSettingViewModel timerType $timerType")

    viewModelScope.launch {
      val color = when (timerType) {
        "flash" -> preferences.flashColorFlow.first()
        "secondary" -> preferences.secondaryColorFlow.first()
        else -> preferences.haloColourFlow.first()
      }
      colorPickerColorState.value = HsvColor.from(color)
      hexText = String.format("#%06X", (0xFFFFFF and color.toArgb()))
      
      val scale = preferences.bubbleScaleFlow.first()
      settingsTimerPreviewVmc = SettingsTimerPreviewVmc(scale, color, "circle", null, false)
      initialised = true
    }
  }

  fun updateHex(newHex: String) {
    hexText = newHex
    try {
        val color = Color(android.graphics.Color.parseColor(newHex))
        colorPickerColorState.value = HsvColor.from(color)
    } catch (e: IllegalArgumentException) {
        // Invalid hex, ignore update to color picker
    }
  }
  
  fun updateColorFromPicker(hsvColor: HsvColor) {
      colorPickerColorState.value = hsvColor
      val color = hsvColor.toColor()
      hexText = String.format("#%06X", (0xFFFFFF and color.toArgb()))
  }

  fun saveDefaultHaloColor() {
    viewModelScope.launch {
      if (timerType == "flash") {
          preferences.updateFlashColor(colorPickerColorState.value.toColor())
      } else if (timerType == "secondary") {
          preferences.updateSecondaryColor(colorPickerColorState.value.toColor())
      } else {
          preferences.updateHaloColour(colorPickerColorState.value.toColor())
      }
    }
  }

  fun okButtonClick(ifPremiumCallback: () -> Unit) {
    viewModelScope.launch {
      logd("saveHaloColorClick")
      if (premiumFlow.first()) {
        ifPremiumCallback()
      } else {
        premiumVmc.showPurchaseDialog = true
      }
    }
  }
}