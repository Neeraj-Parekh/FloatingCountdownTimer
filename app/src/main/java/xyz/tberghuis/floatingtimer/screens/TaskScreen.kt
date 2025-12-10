package xyz.tberghuis.floatingtimer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.viewmodels.TaskScreenVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(vm: TaskScreenVm = viewModel()) {
  val navController = LocalNavController.current
  val tasks by vm.tasks.collectAsState(initial = emptyList())
  val currentTab by vm.currentTab.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Task Management") },
        navigationIcon = {
          IconButton(onClick = { navController.navigateUp() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
          }
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = { showAddDialog = true }) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
      }
    }
  ) { padding ->
    Column(modifier = Modifier.padding(padding)) {
      TabRow(selectedTabIndex = when(currentTab) { "Inbox" -> 0; "Work" -> 1; "Personal" -> 2; else -> 0 }) {
        listOf("Inbox", "Work", "Personal").forEachIndexed { index, title ->
          Tab(
            selected = currentTab == title,
            onClick = { vm.currentTab.value = title },
            text = { Text(title) }
          )
        }
      }
      
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(tasks) { task ->
          Card(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(task.name, style = MaterialTheme.typography.titleMedium)
                Text("${task.durationSeconds / 60} mins", style = MaterialTheme.typography.bodyMedium)
              }
              IconButton(onClick = { vm.deleteTask(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
              }
            }
          }
        }
      }
    }

    if (showAddDialog) {
      AddTaskDialog(
        initialArea = currentTab,
        onDismiss = { showAddDialog = false },
        onConfirm = { name, duration, area ->
          // If area is Inbox, isInbox = true. Else false.
          // Wait, logic: "Inbox" tab means Brain Dump. "Work" tab means specific area.
          val isInbox = area == "Inbox"
          val saveArea = if (isInbox) "General" else area
          vm.addTask(name, duration * 60, android.graphics.Color.GREEN, saveArea, isInbox)
          showAddDialog = false
        }
      )
    }
  }
}

@Composable
fun AddTaskDialog(initialArea: String, onDismiss: () -> Unit, onConfirm: (String, Int, String) -> Unit) {
  var name by remember { mutableStateOf("") }
  var duration by remember { mutableStateOf("25") }
  // Allow user to change area in dialog? Simplification: Just inherit from current tab.
  
  Dialog(onDismissRequest = onDismiss) {
    Card(modifier = Modifier.padding(16.dp)) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Add New Task to $initialArea", style = MaterialTheme.typography.headlineSmall)
        TextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Task Name") }
        )
        TextField(
          value = duration,
          onValueChange = { duration = it },
          label = { Text("Duration (minutes)") }
        )
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          IconButton(onClick = onDismiss) { Text("Cancel") }
          IconButton(onClick = {
            onConfirm(name, duration.toIntOrNull() ?: 25, initialArea)
          }) { Text("Add") }
        }
      }
    }
  }
}
