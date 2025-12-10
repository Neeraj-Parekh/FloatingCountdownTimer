package xyz.tberghuis.floatingtimer.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.godaddy.android.colorpicker.ClassicColorPicker
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.composables.ColorSettingScreenActions
import xyz.tberghuis.floatingtimer.composables.SettingsTimerPreviewCard
import xyz.tberghuis.floatingtimer.viewmodels.ColorSettingViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSettingScreen(
  vm: ColorSettingViewModel = viewModel()
) {
  val navController = LocalNavController.current

  val topBarTitle = when (vm.timerType) {
    "stopwatch" -> stringResource(R.string.stopwatch_timer_color)
    "countdown" -> stringResource(R.string.countdown_timer_color)
    "secondary" -> "Secondary Color"
    else -> stringResource(R.string.default_timer_color)
  }

  Scaffold(
    modifier = Modifier,
    topBar = {
      TopAppBar(
        title = { Text(topBarTitle) },
        navigationIcon = {
          IconButton(onClick = {
            navController.navigateUp()
          }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
          }
        },
        modifier = Modifier,
      )
    },
    snackbarHost = {},
  ) { padding ->
    ColorSettingScreenContent(padding)
  }
}

@Composable
fun ColorSettingScreenContent(
  padding: PaddingValues,
  vm: ColorSettingViewModel = viewModel()
) {
  if (!vm.initialised) {
    return
  }

  Column(
    modifier = Modifier
      .padding(padding)
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {

    Column(
      modifier = Modifier
        .widthIn(0.dp, 350.dp)
        .padding(15.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      SettingsTimerPreviewCard(vm.settingsTimerPreviewVmc)
      ClassicColorPicker(
        modifier = Modifier
          .height(300.dp)
          .fillMaxWidth(),
        colorState = vm.colorPickerColorState
      )
      
      androidx.compose.material3.OutlinedTextField(
        value = vm.hexText,
        onValueChange = { vm.updateHex(it) },
        label = { Text("Hex Code") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )
      
      ColorSettingScreenActions()
    }
  }

  // this is wack
  LaunchedEffect(vm) {
    snapshotFlow {
      vm.colorPickerColorState.value
    }.collect {
      // Create loop break check?
      // When picker updates, we want to update preview AND hex text (if not editing hex).
      // We moved this logic to updateColorFromPicker in VM? 
      // No, ClassicColorPicker updates the state directly.
      // So checking changes here is correct.
      // But updating hexText here while user types hex would cause loop/cursor jump if we are not careful.
      // VM updateHex updates state.
      // If we blindly update hexText from state here, it might conflict.
      // Let's rely on VM to sync hexText only when NOT driven by updateHex? 
      // Actually, ClassicColorPicker writes to colorPickerColorState.
      // So we need to sync hexText when that changes.
      // But if change came from updateHex, hexText is already set.
      // We can convert back and check equality.
      
      val color = it.toColor()
      vm.settingsTimerPreviewVmc.haloColor = color
      
      val newHex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
      // Only update if significantly different to allow typing?
      // Or simply: updateHex updates state, state updates hexText.
      // If user types "#F", state might not be valid, so state doesn't update, hexText stays "#F".
      // If user types "#FF0000", state updates, this flow triggers, sets hexText to "#FF0000". Consistent.
      if (vm.hexText.uppercase() != newHex) {
          // Ideally we check if the color represented by vm.hexText is different from current color
          // to avoid formatting wars.
          try {
             val currentHexColor = Color(android.graphics.Color.parseColor(vm.hexText))
             if (currentHexColor != color) {
                 vm.hexText = newHex
             }
          } catch(e: Exception) {
             vm.hexText = newHex
          }
      }
    }
  }
}