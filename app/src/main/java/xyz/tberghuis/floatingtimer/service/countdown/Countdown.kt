package xyz.tberghuis.floatingtimer.service.countdown

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.SavedTimer
import xyz.tberghuis.floatingtimer.logd
import xyz.tberghuis.floatingtimer.service.Bubble
import xyz.tberghuis.floatingtimer.service.FloatingService
import kotlin.math.roundToInt

class Countdown(
  private val service: FloatingService,
  initialDurationSeconds: Int,
  bubbleSizeScaleFactor: Float,
  haloColor: Color,
  secondaryColor: Color,
  timerShape: String,
  label: String?,
  isBackgroundTransparent: Boolean,
  savedTimer: SavedTimer? = null,
  start: Boolean = false,
  val isTaskMode: Boolean = false
) : Bubble(
  service,
  bubbleSizeScaleFactor,
  haloColor,
  secondaryColor,
  timerShape,
  label,
  isBackgroundTransparent,
  savedTimer
) {
  override var label: String? = label
  var durationSeconds = initialDurationSeconds
  var countdownSeconds by mutableIntStateOf(durationSeconds)
  
  fun updateTask(task: xyz.tberghuis.floatingtimer.data.Task) {
      durationSeconds = task.durationSeconds
      countdownSeconds = durationSeconds
      label = task.name
      // TODO: Update color? 
      // Bubble has haloColor (val). We might need to make it var too.
      // But for now let's stick to label and duration.
  }

  val timerState = MutableStateFlow<TimerState>(
    if (start) TimerStateRunning else TimerStatePaused
  )
  private var countDownTimer: CountDownTimer? = null

  override fun exit() {
    service.alarmController.stopAlarm(this)
    countDownTimer?.cancel()
    super.exit()
  }

  override fun reset() {
    countdownSeconds = durationSeconds
    timerState.value = TimerStatePaused
  }

  override fun onTap() {
    logd("click target onclick")
    when (timerState.value) {
      is TimerStatePaused -> {
        // on first tap update saved position
        if (countdownSeconds == durationSeconds) {
          saveTimerPositionIfNull()
        }
        timerState.value = TimerStateRunning
      }

      is TimerStateRunning -> {
        timerState.value = TimerStatePaused
      }

      is TimerStateFinished -> {
        reset()
      }
    }
  }

  init {
    manageAlarm()
    manageCountdownTimer()
  }

  private fun manageAlarm() {
    service.scope.launch {
      timerState.collectLatest {
        logd("timerState collectLatest $it")
        when (it) {
          TimerStateFinished -> {
            service.alarmController.startAlarm(this@Countdown)
          }

          TimerStateRunning -> {
          }

          TimerStatePaused -> {
            service.alarmController.stopAlarm(this@Countdown)
          }
        }
      }
    }
  }

  private fun manageCountdownTimer() {
    logd("manageCountdownTimer")
    // Main + immediate??? prevent ANRs???
    service.scope.launch(Dispatchers.Main) {

      timerState.collectLatest {
        countDownTimer?.cancel()
        if (it is TimerStateRunning) {
          // todo make timer more accurate
          // when pause store countdownMillis
          countDownTimer =
            object : CountDownTimer(countdownSeconds * 1000L, 1000) {
              override fun onTick(millisUntilFinished: Long) {
                countdownSeconds = (millisUntilFinished / 1000f).roundToInt()
                
                // Update Widget
                val min = countdownSeconds / 60
                val sec = countdownSeconds % 60
                val timeString = String.format("%02d:%02d", min, sec)
                xyz.tberghuis.floatingtimer.widget.TimerWidgetProvider.updateAllWidgets(
                    service, 
                    timeString, 
                    label ?: "Timer"
                )
              }

              override fun onFinish() {
                countdownSeconds = 0
                timerState.value = TimerStateFinished
                
                // Update Widget to 00:00
                xyz.tberghuis.floatingtimer.widget.TimerWidgetProvider.updateAllWidgets(
                    service, 
                    "00:00", 
                    label ?: "Timer"
                )
                
                // Log session
                val completionTime = System.currentTimeMillis()
                val startTime = completionTime - (durationSeconds * 1000L)
                service.scope.launch {
                    service.sessionRepository.logSession(
                        xyz.tberghuis.floatingtimer.data.SessionLog(
                            taskName = label ?: "Timer",
                            startTime = startTime,
                            endTime = completionTime,
                            durationSeconds = durationSeconds
                        )
                    )
                }

                if (isTaskMode) {
                   service.scope.launch {
                       service.taskRepository.moveNext()
                       val nextTask = service.taskRepository.getCurrentTask()
                       if (nextTask != null) {
                         updateTask(nextTask)
                       }
                   }
                }
              }
            }.start()
        }
      }
    }
  }
}