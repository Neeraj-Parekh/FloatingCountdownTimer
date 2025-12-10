package xyz.tberghuis.floatingtimer.service

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.preferencesRepository
import xyz.tberghuis.floatingtimer.service.countdown.Countdown
import xyz.tberghuis.floatingtimer.util.uriToRingtone

class FtAlarmController(
  private val floatingService: FloatingService
) {
  private val prefs = floatingService.application.preferencesRepository

  private val vibrator = initVibrator()
  private var ringtone: Ringtone? = null

  private val finishedCountdowns = MutableStateFlow(setOf<Countdown>())
  private val alarmRunning = MutableStateFlow(false)
  private var looping: Boolean? = null
  private var vibrate: Boolean? = null
  private var sound: Boolean? = null
  private var ringtoneDuration: Long? = null
  private var customName: String? = null
  private var customSoundName: String? = null // Legacy support per user request
  private var flashEnabled: Boolean = false
  private var flashColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Red

  init {
    watchFinishedCountdowns()
    watchRingtoneUri()
    watchAlarmRunning()

    floatingService.scope.launch {
      prefs.soundFlow.collectLatest {
        sound = it
      }
    }
    floatingService.scope.launch {
      prefs.vibrationFlow.collectLatest {
        vibrate = it
      }
    }
    floatingService.scope.launch {
      prefs.loopingFlow.collectLatest {
        looping = it
      }
    }
    floatingService.scope.launch {
      prefs.customSoundNameFlow.collectLatest {
        customName = it
        customSoundName = it
      }
    }
    floatingService.scope.launch {
      prefs.flashEnabledFlow.collectLatest {
        flashEnabled = it
      }
    }
    floatingService.scope.launch {
      prefs.flashColorFlow.collectLatest {
        flashColor = it
      }
    }
  }

  fun stopAlarmFromOverlay() {
      // Logic to stop alarm
      alarmRunning.value = false
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun watchRingtoneUri() {
    floatingService.scope.launch {
      prefs.alarmRingtoneUriFlow.collectLatest { uri ->
        uri?.let {
          alarmRunning.value = false
          ringtoneDuration = getRingtoneDuration(floatingService, uri)
          // I should use withContext(Main) ???
          ringtone =
            uriToRingtone(floatingService.application, uri.toUri())?.apply {
              audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .build()
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isHapticGeneratorEnabled = false
              }
              if (Build.VERSION.SDK_INT >= 28) {
                isLooping = true
              }
            }
        }
      }
    }
  }

  private fun initVibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager =
        floatingService.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibratorManager.defaultVibrator
    } else {
      @Suppress("DEPRECATION") floatingService.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
  }

  fun startAlarm(c: Countdown) {
    finishedCountdowns.value += c
  }

  fun stopAlarm(c: Countdown) {
    finishedCountdowns.value -= c
  }

  private fun watchAlarmRunning() {
    // do I need Main.immediate dispatcher? .play() .vibrate()
    floatingService.scope.launch(Main.immediate) {
      try {
        alarmRunning.collectLatest { running ->
          when (running) {
            true -> {
              if (flashEnabled) {
                  floatingService.flashOverlayController.showFlashOverlay(flashColor)
              }
// Imports at top...
// Add import xyz.tberghuis.floatingtimer.service.audio.CustomSoundManager

              if (sound == true) {
                if (customName != null) {
                    val name = customName // local val for smart cast
                    val customSoundManager = xyz.tberghuis.floatingtimer.service.audio.CustomSoundManager(floatingService.application)
                    val soundFile = name?.let { customSoundManager.getSoundFile(it) }
                    
                    if (soundFile != null) {
                         floatingService.soundManager.playFile(soundFile, looping ?: true)
                    } else {
                        // Fallback to resource if not found
                        val resId = name?.let { floatingService.application.resources.getIdentifier(it, "raw", floatingService.application.packageName) }
                        if (resId != null && resId != 0) {
                            floatingService.soundManager.playSound(resId, looping ?: true)
                        }
                    }
                } else {
                   ringtone?.play()
                }
              }
              if (vibrate == true) {
                vibrator.vibrate(
                  VibrationEffect.createWaveform(
                    longArrayOf(1500, 200), intArrayOf(255, 0), 0
                  )
                )
              }
              if (sound == false && vibrate == false && !flashEnabled) {
                alarmRunning.value = false
                return@collectLatest
              }
              if (looping == false && ringtoneDuration != null && customSoundName == null && !flashEnabled) {
                // If flashing is enabled, we probably want to keep going until user dismisses?
                // The requirement says "Option to toggle flash on/off".
                // If sound ends but flash is on, should we stop?
                // Visual alerts usually persist.
                // For now, if flash is on, we don't auto-stop. User must dismiss.
                
                // But if only sound is non-looping and flash is off:
                delay(ringtoneDuration!!)
                alarmRunning.value = false
              }
            }

            false -> {
              vibrator.cancel()
              ringtone?.stop()
              floatingService.soundManager.stop()
              floatingService.flashOverlayController.hideFlashOverlay()
            }
          }
        }
      } finally {
        // cancellation exception
        vibrator.cancel()
        ringtone?.stop()
        floatingService.soundManager.stop()
        floatingService.flashOverlayController.hideFlashOverlay()
      }
    }
  }

  private fun watchFinishedCountdowns() {
    floatingService.scope.launch {
      finishedCountdowns.collectLatest {
        when (it.size) {
          0 -> {
            alarmRunning.value = false
          }

          else -> {
            alarmRunning.value = true
          }
        }
      }
    }
  }
}

private fun getRingtoneDuration(context: Context, ringtoneUri: String): Long? {
  // will this sometimes throw without READ_EXTERNAL_STORAGE permission???
  var duration: Long? = null
  try {
    val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, Uri.parse(ringtoneUri))
    duration = mediaPlayer?.duration?.toLong()
    mediaPlayer?.release()
  } catch (e: Exception) {
    Log.e("FtAlarmController", "error: ${e.message}")
  }
  return duration
}