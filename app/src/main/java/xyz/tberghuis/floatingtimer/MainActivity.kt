package xyz.tberghuis.floatingtimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import xyz.tberghuis.floatingtimer.data.preferencesRepository
import xyz.tberghuis.floatingtimer.iap.billingClientWrapper
import xyz.tberghuis.floatingtimer.ui.theme.FloatingTimerTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
  private fun checkPremium() {
    val preferencesRepository = application.preferencesRepository
    lifecycleScope.launch(IO) {
      val purchased =
        application.billingClientWrapper.checkPremiumPurchased() ?: return@launch
      preferencesRepository.updateHaloColourPurchased(purchased)
      logd("MainActivity checkPremium purchased $purchased")
      if (!purchased) {
        preferencesRepository.resetHaloColour()
        preferencesRepository.resetBubbleScale()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    logd("onCreate")
    checkPremium()
    enableEdgeToEdge()
    setContent {
      val themeMode = application.preferencesRepository.themeModeFlow
          .collectAsState(initial = "system").value
      
      val darkTheme = when(themeMode) {
          "light" -> false
          "dark" -> true
          else -> isSystemInDarkTheme()
      }
      
      FloatingTimerTheme(darkTheme = darkTheme) {
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Use theme background
        ) {
            // ...
          FtNavHost()
        }
      }
    }
  }
}

val LocalNavController = compositionLocalOf<NavHostController> {
  error("CompositionLocal LocalNavController not present")
}