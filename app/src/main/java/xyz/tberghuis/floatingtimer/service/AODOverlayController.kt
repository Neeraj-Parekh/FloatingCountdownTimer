package xyz.tberghuis.floatingtimer.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.preferencesRepository
import xyz.tberghuis.floatingtimer.logd

class AODOverlayController(private val service: FloatingService) {
    
    private var aodView: View? = null
    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    
    fun showAODOverlay() {
        if (aodView != null) return // Already showing

        service.scope.launch {
            val aodEnabled = service.application.preferencesRepository.aodEnabledFlow.first()
            if (!aodEnabled) return@launch
            
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.CENTER

            val view = ComposeView(service).apply {
                setContent {
                    AODScreen()
                }
            }
            
            try {
                windowManager.addView(view, params)
                aodView = view
            } catch (e: Exception) {
                logd("Error adding AOD view: ${e.message}")
            }
        }
    }

    fun hideAODOverlay() {
        aodView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                logd("Error removing AOD view: ${e.message}")
            }
            aodView = null
        }
    }
    
    @Composable
    fun AODScreen() {
        // Simple AOD Screen: Black background, digital time in center
        // In a real AOD, we should move text slightly to prevent burn-in.
        // For this MVP, static center is fine or simple constant movement.
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // We could show the active timer here if we link to AlarmController or TimerState
            // For now, let's just show a clear message or clock
            // ideally we want to show the specific running timer. 
            // Accessing active timers is complex; let's show a placeholder "Floating Timer Active"
            // or if feasible, observe the same state as OverlayController.
            
            Text("Floating Timer Active", color = Color.White, fontSize = 24.sp)
        }
    }
}
