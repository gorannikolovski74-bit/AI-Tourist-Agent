package com.goran.aitouristagent.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goran.aitouristagent.domain.Activity
import com.goran.aitouristagent.domain.Day

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    tripId: String,
    onBack: () -> Unit,
    viewModel: ItineraryViewModel = hiltViewModel(),
) {
    val trip by viewModel.trip.collectAsState()
    val days by viewModel.days.collectAsState()
    var showAddDayDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.name ?: "Итинерар") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDayDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Додади ден")
            }
        },
    ) { padding ->
        if (days.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Нема денови сè уште.\nДодади го првиот ден од патувањето.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                items(days, key = { it.id }) { day ->
                    DayCard(
                        day = day,
                        viewModel = viewModel,
                        onDeleteDay = { viewModel.deleteDay(day.id) },
                    )
                }
            }
        }
    }

    if (showAddDayDialog) {
        AddDayDialog(
            onDismiss = { showAddDayDialog = false },
            onAdd = { date, title, subtitle ->
                viewModel.addDay(date, title, subtitle)
                showAddDayDialog = false
            },
        )
    }
}

@Composable
private fun DayCard(day: Day, viewModel: ItineraryViewModel, onDeleteDay: () -> Unit) {
    val activities by remember(day.id) { viewModel.observeActivities(day.id) }
        .collectAsState(initial = emptyList())
    var showAddActivityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${day.date} · ${day.title}", style = MaterialTheme.typography.titleLarge)
                if (day.subtitle.isNotBlank()) {
                    Text(
                        text = day.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDeleteDay) {
                Icon(Icons.Filled.Delete, contentDescription = "Избриши ден", tint = MaterialTheme.colorScheme.error)
            }
        }

        activities.forEach { activity ->
            ActivityRow(activity = activity, onDelete = { viewModel.deleteActivity(activity.id) })
        }

        TextButton(onClick = { showAddActivityDialog = true }) {
            Text("+ Додади активност")
        }
    }

    if (showAddActivityDialog) {
        AddActivityDialog(
            onDismiss = { showAddActivityDialog = false },
            onAdd = { text, price, mapsUrl ->
                viewModel.addActivity(day.id, text, price, mapsUrl)
                showAddActivityDialog = false
            },
        )
    }
}

@Composable
private fun ActivityRow(activity: Activity, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = activity.text, style = MaterialTheme.typography.bodyLarge)
            if (activity.price != null) {
                Text(
                    text = "€${activity.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Избриши активност",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun AddDayDialog(onDismiss: () -> Unit, onAdd: (date: String, title: String, subtitle: String) -> Unit) {
    var date by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    val isValid = date.isNotBlank() && title.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нов ден") },
        text = {
            Column {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Датум (ГГГГ-ММ-ДД)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Наслов") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Поднаслов (опционално)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(enabled = isValid, onClick = { onAdd(date.trim(), title.trim(), subtitle.trim()) }) {
                Text("Додади")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Откажи") }
        },
    )
}

@Composable
private fun AddActivityDialog(
    onDismiss: () -> Unit,
    onAdd: (text: String, price: Double?, mapsUrl: String?) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var mapsUrl by remember { mutableStateOf("") }
    val isValid = text.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова активност") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Активност") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена (опционално)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = mapsUrl,
                    onValueChange = { mapsUrl = it },
                    label = { Text("Google Maps линк (опционално)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    onAdd(text.trim(), price.toDoubleOrNull(), mapsUrl.trim().ifBlank { null })
                },
            ) {
                Text("Додади")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Откажи") }
        },
    )
}
