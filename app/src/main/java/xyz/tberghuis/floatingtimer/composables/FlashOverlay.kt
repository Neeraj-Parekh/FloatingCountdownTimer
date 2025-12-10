package xyz.tberghuis.floatingtimer.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun FlashOverlay(
    flashColor: Color,
    onDismiss: () -> Unit
) {
    var isFlashing by remember { mutableStateOf(false) }
    
    val animatedColor by animateColorAsState(
        targetValue = if (isFlashing) flashColor.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )

    LaunchedEffect(Unit) {
        isFlashing = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedColor)
            .clickable { onDismiss() }
    )
}
