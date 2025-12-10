package xyz.tberghuis.floatingtimer.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.data.Task
import xyz.tberghuis.floatingtimer.viewmodels.TaskScreenVm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(vm: TaskScreenVm = viewModel()) {
    val navController = LocalNavController.current
    // We reuse TaskScreenVm to get the list of tasks. 
    // Ideally we should have a separate VM or share it, but for simplicity reusing or creating new instance is fine if it uses Repo.
    // NOTE: TaskScreenVm 'tasks' flow depends on 'currentTab'. We want ALL active tasks for planning?
    // Or just the queue? Let's assume we want to visualize the "Work" queue for now or user can switch tabs.
    // Let's create a dedicated VM or just query all non-inbox tasks?
    // For now, let's look at the current filtered list from VM.
    
    val tasks by vm.tasks.collectAsState(initial = emptyList())
    val currentTime = System.currentTimeMillis()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visual Planner") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "Projected Timeline",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                var accumulatingTime = currentTime
                
                items(tasks) { task ->
                    val startTime = accumulatingTime
                    val durationMillis = task.durationSeconds * 1000L
                    val endTime = startTime + durationMillis
                    accumulatingTime = endTime

                    TimelineTaskItem(
                        task = task,
                        startTime = startTime,
                        endTime = endTime
                    )
                }
                
                item {
                    val finishTime = accumulatingTime
                     Text(
                        "Estimated Finish: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(finishTime))}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineTaskItem(task: Task, startTime: Long, endTime: Long) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Row(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        // Time Column
        Column(
            modifier = Modifier.width(60.dp).padding(vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(dateFormat.format(Date(startTime)), style = MaterialTheme.typography.bodySmall)
            // Text(dateFormat.format(Date(endTime)), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        
        // Line
        Box(modifier = Modifier.width(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(size.width/2, 0f),
                    end = Offset(size.width/2, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                drawCircle(
                    color = Color(task.color),
                    radius = 4.dp.toPx(),
                    center = Offset(size.width/2, 16.dp.toPx()) // Align with start time text roughly
                )
            }
        }
        
        // Task Card
        Card(
            modifier = Modifier.weight(1f).padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(task.name, style = MaterialTheme.typography.bodyMedium)
                Text("${task.durationSeconds / 60} mins", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
