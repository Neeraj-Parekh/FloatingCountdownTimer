package xyz.tberghuis.floatingtimer.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.tberghuis.floatingtimer.LocalNavController
import xyz.tberghuis.floatingtimer.R
import xyz.tberghuis.floatingtimer.viewmodels.ReflectionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(
    vm: ReflectionViewModel = viewModel()
) {
    val navController = LocalNavController.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Reflection") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        ReflectionContent(padding, vm)
    }
}

@Composable
fun ReflectionContent(padding: PaddingValues, vm: ReflectionViewModel) {
    var wentWell by remember { mutableStateOf("") }
    var challenges by remember { mutableStateOf("") }
    var energyLevel by remember { mutableFloatStateOf(3f) }
    var priorities by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val reflections by vm.allReflections.collectAsState(initial = emptyList())
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("End-of-Day Review", style = MaterialTheme.typography.headlineSmall)
        }
        
        item {
            OutlinedTextField(
                value = wentWell,
                onValueChange = { wentWell = it },
                label = { Text("What went well today?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
        
        item {
            OutlinedTextField(
                value = challenges,
                onValueChange = { challenges = it },
                label = { Text("What was challenging?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            Text("Energy Level: ${energyLevel.toInt()}/5")
            Slider(
                value = energyLevel,
                onValueChange = { energyLevel = it },
                valueRange = 1f..5f,
                steps = 3
            )
        }
        
        item {
            OutlinedTextField(
                value = priorities,
                onValueChange = { priorities = it },
                label = { Text("Tomorrow's Top 3 Priorities") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Other Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
        
        item {
            Button(
                onClick = {
                    vm.addReflection(wentWell, challenges, energyLevel.toInt(), priorities, notes)
                    wentWell = ""
                    challenges = ""
                    priorities = ""
                    notes = ""
                    energyLevel = 3f
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Reflection")
            }
        }
        
        item {
            Text("History", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }

        items(reflections) { reflection ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(dateFormat.format(Date(reflection.date)), style = MaterialTheme.typography.labelSmall)
                    Text("Wins: ${reflection.wentWell}", style = MaterialTheme.typography.bodyMedium)
                    if (reflection.prioritiesTomorrow.isNotEmpty()) {
                        Text("Tomorrow: ${reflection.prioritiesTomorrow}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
