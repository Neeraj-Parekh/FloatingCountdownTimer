package xyz.tberghuis.floatingtimer.composables

import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.ImeAction
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.logd
import xyz.tberghuis.floatingtimer.ui.theme.*
import xyz.tberghuis.floatingtimer.viewmodels.CountdownScreenVm
import xyz.tberghuis.floatingtimer.viewmodels.SharedVm

@Preview
@Composable
fun CreateCountdownCard() {
  val vm: CountdownScreenVm = viewModel()
  val focusManager = LocalFocusManager.current
  val context = LocalContext.current
  val sharedVm: SharedVm = viewModel(context as ComponentActivity)
  
  // Premium gradient card
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = AccentBlue.copy(alpha = 0.3f),
        spotColor = AccentBlue.copy(alpha = 0.3f)
      )
      .clip(RoundedCornerShape(24.dp))
      .background(
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
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
        text = stringResource(id = R.string.countdown),
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center
        )
      )
      
      Spacer(modifier = Modifier.height(20.dp))
      
      // Time input row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        CountdownTimeField(vm.hours, stringResource(R.string.hours), imeAction = ImeAction.Next)
        Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
        CountdownTimeField(
          vm.minutes,
          stringResource(R.string.minutes),
          "CountdownMinutes",
          imeAction = ImeAction.Next
        )
        Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
        CountdownTimeField(vm.seconds, stringResource(R.string.seconds))
      }

      Spacer(modifier = Modifier.height(16.dp))
      
      // Shape selection
      TimerShapeChoice(vm)
      
      // Options
      CountdownOptions()
      
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
          ChangeTimerColorButton("change_color/countdown", vm.haloColor)
        }
        
        // Save button
        IconButton(
          onClick = {
            focusManager.clearFocus()
            vm.addToSaved()
          },
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AccentPurple.copy(alpha = 0.3f))
            .border(2.dp, AccentPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
          Icon(
            Icons.Default.Save,
            contentDescription = "Save",
            tint = Color.White
          )
        }
        
        // Create button - Premium gradient
        CreateCountdownButton()
      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateCountdownButton() {
  val vm: CountdownScreenVm = viewModel()
  val focusManager = LocalFocusManager.current
  val context = LocalContext.current
  val sharedVm: SharedVm = viewModel(context as ComponentActivity)
  
  Button(
    onClick = {
      logd("create")
      focusManager.clearFocus()
      if (!Settings.canDrawOverlays(context)) {
        sharedVm.showGrantOverlayDialog = true
        return@Button
      }
      vm.countdownButtonClick()
    },
    modifier = Modifier
      .semantics { testTagsAsResourceId = true }
      .testTag("CreateCountdownButton")
      .height(48.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Transparent,
      contentColor = Color.White
    ),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    shape = RoundedCornerShape(12.dp)
  ) {
    Box(
      modifier = Modifier
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(AccentOrange, GradientOrangeEnd)
          ),
          shape = RoundedCornerShape(12.dp)
        )
        .padding(horizontal = 24.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stringResource(R.string.create),
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color.White
      )
    }
  }
}

@Composable
fun ColumnScope.CountdownOptions() {
  val vm: CountdownScreenVm = viewModel()
  val nav = LocalNavController.current

  // doitwrong
  val vibration = vm.vibrationFlow.collectAsState(false)
  val sound = vm.soundFlow.collectAsState(false)

  Column(
    modifier = Modifier
      .width(IntrinsicSize.Max)
      .align(Alignment.CenterHorizontally)
  ) {
    BackgroundTransCheckbox(vm = vm)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
        checked = vibration.value,
        onCheckedChange = {
          vm.updateVibration(it)
        }
      )
      Text(stringResource(R.string.vibration))
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
        checked = sound.value,
        onCheckedChange = {
          vm.updateSound(it)
        }
      )
      Text("${stringResource(R.string.sound)} ")
      Text(
        text = vm.currentRingtoneVmc.currentRingtoneTitle,
        modifier = Modifier
          .clickable {
            nav.navigate("countdown_ringtone")
          },
        color = MaterialTheme.colorScheme.primary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline)
      )
    }
  }
}