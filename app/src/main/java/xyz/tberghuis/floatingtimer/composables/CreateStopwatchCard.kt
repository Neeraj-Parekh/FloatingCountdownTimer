package xyz.tberghuis.floatingtimer.composables

import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.logd
import xyz.tberghuis.floatingtimer.ui.theme.*
import xyz.tberghuis.floatingtimer.viewmodels.SharedVm
import xyz.tberghuis.floatingtimer.viewmodels.StopwatchScreenVm

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateStopwatchCard() {
  val context = LocalContext.current
  val sharedVm: SharedVm = viewModel(context as ComponentActivity)
  val vm: StopwatchScreenVm = viewModel()

  // Premium gradient card - Purple theme for stopwatch
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = AccentPurple.copy(alpha = 0.3f),
        spotColor = AccentPurple.copy(alpha = 0.3f)
      )
      .clip(RoundedCornerShape(24.dp))
      .background(
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFF1A1225),
            Color(0xFF2D1B4E),
            Color(0xFF1E1338)
          ),
          start = Offset(0f, 0f),
          end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
      )
      .border(
        width = 1.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.05f)
          )
        ),
        shape = RoundedCornerShape(24.dp)
      )
  ) {
    Column(
      modifier = Modifier.padding(20.dp)
    ) {
      // Header
      Text(
        text = stringResource(id = R.string.stopwatch),
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center
        )
      )
      
      Spacer(modifier = Modifier.height(20.dp))
      
      // Shape selection
      TimerShapeChoice(vm)
      
      // Background transparent checkbox
      BackgroundTransCheckbox(Modifier.align(Alignment.CenterHorizontally), vm = vm)
      
      Spacer(modifier = Modifier.height(16.dp))
      
      // Action buttons row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Color picker button
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(vm.haloColor)
            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
          ChangeTimerColorButton("change_color/stopwatch", vm.haloColor)
        }
        
        // Save button
        IconButton(
          onClick = { vm.addToSaved() },
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AccentBlue.copy(alpha = 0.3f))
            .border(2.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
          Icon(
            Icons.Default.Save,
            contentDescription = "Save",
            tint = Color.White
          )
        }
        
        // Create button - Premium purple gradient
        Button(
          onClick = {
            logd("start stopwatch")
            if (!Settings.canDrawOverlays(context)) {
              sharedVm.showGrantOverlayDialog = true
              return@Button
            }
            vm.stopwatchButtonClick()
          },
          modifier = Modifier
            .semantics { testTagsAsResourceId = true }
            .testTag("create_stopwatch")
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
          ),
          contentPadding = PaddingValues(0.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Box(
            modifier = Modifier
              .background(
                brush = Brush.horizontalGradient(
                  colors = listOf(AccentPurple, Color(0xFFAB47BC))
                ),
                shape = RoundedCornerShape(12.dp)
              )
              .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(id = R.string.create),
              fontWeight = FontWeight.SemiBold,
              fontSize = 16.sp,
              color = Color.White
            )
          }
        }
      }
    }
  }
}