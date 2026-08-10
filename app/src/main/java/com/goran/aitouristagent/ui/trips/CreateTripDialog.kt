package com.goran.aitouristagent.ui.trips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateTripDialog(
    onDismiss: () -> Unit,
    onCreate: (
        name: String,
        destination: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        currency: String,
        budgetTotal: Double,
    ) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var travelers by remember { mutableStateOf("1") }
    var currency by remember { mutableStateOf("EUR") }
    var budgetTotal by remember { mutableStateOf("") }

    val travelersValue = travelers.toIntOrNull()
    val budgetValue = budgetTotal.toDoubleOrNull()
    val isValid = name.isNotBlank() && destination.isNotBlank() &&
        startDate.isNotBlank() && endDate.isNotBlank() &&
        currency.isNotBlank() &&
        travelersValue != null && travelersValue > 0 &&
        budgetValue != null && budgetValue >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ново патување") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Име на патувањето") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Дестинација") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Почеток (ГГГГ-ММ-ДД)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp, bottom = 8.dp),
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Крај (ГГГГ-ММ-ДД)") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp, bottom = 8.dp),
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = travelers,
                        onValueChange = { travelers = it },
                        label = { Text("Патници") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp, bottom = 8.dp),
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Валута") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp, bottom = 8.dp),
                    )
                }
                OutlinedTextField(
                    value = budgetTotal,
                    onValueChange = { budgetTotal = it },
                    label = { Text("Вкупен буџет") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    onCreate(
                        name.trim(),
                        destination.trim(),
                        startDate.trim(),
                        endDate.trim(),
                        travelersValue ?: 1,
                        currency.trim(),
                        budgetValue ?: 0.0,
                    )
                },
            ) {
                Text("Креирај")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Откажи")
            }
        },
    )
}
