package xyz.tberghuis.floatingtimer.service

import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import xyz.tberghuis.floatingtimer.composables.FlashOverlay
import xyz.tberghuis.floatingtimer.logd

class FlashOverlayController(private val service: FloatingService) {
    private var overlayView: ComposeView? = null

    fun showFlashOverlay(color: Color) {
        if (overlayView != null) return // Already showing

        logd("showFlashOverlay")
        overlayView = ComposeView(service).apply {
            setViewTreeLifecycleOwner(service)
            setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            })
            setViewTreeSavedStateRegistryOwner(service)
            setContent {
                FlashOverlay(flashColor = color, onDismiss = {
                    hideFlashOverlay()
                    // Stop alarm too if flashing is dismissed?
                    // Ideally yes, user acknowledged.
                    service.alarmController.stopAlarmFromOverlay()
                })
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        try {
            service.ftWindowManager.addView(overlayView!!, params)
        } catch (e: Exception) {
            logd("Error adding flash overlay: ${e.message}")
            overlayView = null
        }
    }

    fun hideFlashOverlay() {
        if (overlayView != null) {
            try {
                service.ftWindowManager.removeView(overlayView!!)
            } catch (e: Exception) {
                logd("Error removing flash overlay: ${e.message}")
            }
            overlayView = null
        }
    }
}
