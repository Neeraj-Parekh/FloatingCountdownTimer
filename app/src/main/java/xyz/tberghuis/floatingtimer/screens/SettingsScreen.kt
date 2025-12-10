package xyz.tberghuis.floatingtimer.screens

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.tberghuis.floatingtimer.DEFAULT_HALO_COLOR
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.logd
import xyz.tberghuis.floatingtimer.openGithubIssues
import xyz.tberghuis.floatingtimer.openPlayStorePage
import xyz.tberghuis.floatingtimer.viewmodels.SettingsScreenVm
import xyz.tberghuis.floatingtimer.viewmodels.SharedVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
  val navController = LocalNavController.current
  Scaffold(
    modifier = Modifier,
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
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
    SettingsScreenContent(padding)
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreenContent(
  padding: PaddingValues,
  vm: SettingsScreenVm = viewModel(),
  sharedVm: SharedVm = viewModel(LocalActivity.current as ComponentActivity)
) {
  val navController = LocalNavController.current
  val looping by vm.loopingFlow.collectAsState(true)
  val autoStart by vm.autoStartFlow.collectAsState(false)
  val haloColour by vm.haloColourFlow.collectAsState(DEFAULT_HALO_COLOR)
  val purchased by vm.haloColourPurchasedFlow.collectAsState(null)
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .consumeWindowInsets(padding)
      .padding(padding)
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    ListItem(
      headlineContent = { Text(stringResource(R.string.premium_upgrade)) },
      modifier = Modifier.clickable {
        navController.navigate("premium")
      },
      supportingContent = {
        val t = when (purchased) {
          true -> stringResource(R.string.unlocked)
          false -> stringResource(R.string.locked)
          null -> ""
        }
        Text(t)
      },
    )

    HorizontalDivider()
    Text(
      stringResource(R.string.timer),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.change_default_color)) },
      modifier = Modifier
        .semantics { testTagsAsResourceId = true }
        .testTag("change_color")
        .clickable {
          navController.navigate("change_color")
        },
      trailingContent = {
        Icon(
          Icons.Filled.Circle,
          contentDescription = "color",
          modifier = Modifier.size(35.dp),
          tint = haloColour
        )
      },
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.change_size)) },
      modifier = Modifier
        .semantics { testTagsAsResourceId = true }
        .testTag("change_size")
        .clickable {
          navController.navigate("change_size")
        })

    ListItem(
      headlineContent = { Text(stringResource(R.string.auto_start)) },
      trailingContent = {
        Switch(
          checked = autoStart,
          onCheckedChange = {
            logd("onCheckedChange $it")
            vm.updateAutoStart(it)
          },
        )
      },
    )

    HorizontalDivider()
    Text(
      stringResource(R.string.countdown),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.ringtone)) },
      modifier = Modifier.clickable {
        navController.navigate("countdown_ringtone")
      },
      supportingContent = { Text(vm.currentRingtoneVmc.currentRingtoneTitle) },
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.repeating_alarm_sound_vibration)) },
      supportingContent = { Text(stringResource(R.string.off_ringtone_will_only_play_once)) },
      trailingContent = {
        Switch(
          checked = looping,
          onCheckedChange = {
            logd("onCheckedChange $it")
            vm.updateLooping(it)
          },
        )
      },
    )

    HorizontalDivider()
    Text(
      stringResource(R.string.actions),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.cancel_all_timers)) },
      modifier = Modifier.clickable {
        sharedVm.cancelAllTimers()
      })
    ListItem(
      headlineContent = { Text(stringResource(R.string.save_timer_positions)) },
      modifier = Modifier.clickable {
        sharedVm.saveTimerPositions()
      })
      
    ListItem(
      headlineContent = { Text("End-of-Day Reflection") },
      supportingContent = { Text("Journal your productivity") },
      modifier = Modifier.clickable {
        navController.navigate("reflection")
      })

    HorizontalDivider()
    Text(
      "Screen Flash",
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    val flashEnabled by vm.flashEnabledFlow.collectAsState(false)
    val flashColor by vm.flashColorFlow.collectAsState(Color.Red)
    
    ListItem(
      headlineContent = { Text("Enable Screen Flash") },
      trailingContent = {
        Switch(
          checked = flashEnabled,
          onCheckedChange = {
            vm.updateFlashEnabled(it)
          },
        )
      },
    )
    ListItem(
      headlineContent = { Text("Statistics") },
      modifier = Modifier.clickable { navController.navigate("stats") }
    )
    ListItem(
      headlineContent = { Text("Visual Planner") },
      modifier = Modifier.clickable { navController.navigate("planner") }
    )
    ListItem(
      headlineContent = { Text("Backup & Restore") },
      modifier = Modifier.clickable { navController.navigate("backup") }
    )
    ListItem(
      headlineContent = { Text("Flash Color") },
      modifier = Modifier
        .clickable {
          navController.navigate("change_color/flash")
        },
      trailingContent = {
        Icon(
          Icons.Filled.Circle,
          contentDescription = "color",
          modifier = Modifier.size(35.dp),
          tint = flashColor
        )
      },
    )

    val secondaryColor by vm.secondaryColorFlow.collectAsState(Color.Cyan)
    ListItem(
      headlineContent = { Text("Change Secondary Color") },
      supportingContent = { Text("Accent color for visual styles") },
      modifier = Modifier.clickable {
        navController.navigate("change_color/secondary")
      },
      trailingContent = {
        Icon(
          Icons.Filled.Circle,
          contentDescription = "color",
          modifier = Modifier.size(35.dp),
          tint = secondaryColor // Use Collected State
        )
      },
    )
    
    val visualStyle by vm.visualStyleFlow.collectAsState(xyz.tberghuis.floatingtimer.data.TimerVisualStyle.DEFAULT)
    var expanded by remember { mutableStateOf(false) }

    ListItem(
      headlineContent = { Text("Timer Visual Style") },
      trailingContent = {
        Box {
            Text(
                visualStyle.name,
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(8.dp)
            )
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                xyz.tberghuis.floatingtimer.data.TimerVisualStyle.values().forEach { style ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(style.name) },
                        onClick = {
                            vm.updateVisualStyle(style)
                            expanded = false
                        }
                    )
                }
            }
        }
      }
    )
    ListItem(
      headlineContent = { Text("Manage Tasks") },
      supportingContent = { Text("Add/Edit Brain Dump tasks") },
      modifier = Modifier.clickable {
        navController.navigate("tasks")
      })

    ListItem(
      headlineContent = { Text("Start Task Mode (Demo)") },
      supportingContent = { Text("Starts the task list sequence") },
      modifier = Modifier.clickable {
        sharedVm.startTaskTimer()
      })

    // Theme and AOD
    HorizontalDivider()
    Text(
      "Display",
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )

    val themeMode by vm.themeModeFlow.collectAsState("system")
    var themeExpanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("App Theme") },
        trailingContent = {
            Box {
                Text(
                    themeMode.replaceFirstChar { it.uppercase() },
                    modifier = Modifier
                        .clickable { themeExpanded = true }
                        .padding(8.dp)
                )
                androidx.compose.material3.DropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    listOf("system", "light", "dark").forEach { mode ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(mode.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                vm.updateThemeMode(mode)
                                themeExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )

    val aodEnabled by vm.aodEnabledFlow.collectAsState(false)
    ListItem(
      headlineContent = { Text("Enable AOD (Always-On Display)") },
      supportingContent = { Text("Show timer overlay on lock screen (Beta)") },
      trailingContent = {
        Switch(
          checked = aodEnabled,
          onCheckedChange = {
            vm.updateAodEnabled(it)
          },
        )
      },
    )




    HorizontalDivider()
    Text(
      "Audio Masking",
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    val audioMaskingEnabled by vm.audioMaskingEnabledFlow.collectAsState(false)
    
    ListItem(
      headlineContent = { Text("Enable White Noise") },
      supportingContent = { Text("Play masking sound during timer (Beta)") },
      trailingContent = {
        Switch(
          checked = audioMaskingEnabled,
          onCheckedChange = {
            vm.updateAudioMaskingEnabled(it)
          },
        )
      },
    )

    HorizontalDivider()
    Text(
      stringResource(R.string.links),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
        .padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.primary,
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.play_store)) },
      modifier = Modifier.clickable {
        context.openPlayStorePage()
      },
      supportingContent = { Text(stringResource(R.string.leave_a_review)) },
      trailingContent = {
        Icon(
          Icons.Default.OpenInBrowser,
          contentDescription = "open",
        )
      },
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.github_issues)) },
      modifier = Modifier.clickable {
        context.openGithubIssues()
      },
      supportingContent = { Text(stringResource(R.string.report_a_bug_or_request_a_feature)) },
      trailingContent = {
        Icon(
          Icons.Default.OpenInBrowser,
          contentDescription = "open",
        )
      }
    )

//    Spacer(
//      Modifier.height(
//        WindowInsets.safeContent.asPaddingValues()
//          .calculateBottomPadding() - WindowInsets.safeDrawing.asPaddingValues()
//          .calculateBottomPadding()
//      )
//    )

    Spacer(
      Modifier.windowInsetsBottomHeight(
        WindowInsets.safeContent
      )
    )

  }
}