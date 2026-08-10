package com.goran.aitouristagent.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goran.aitouristagent.domain.BudgetItem
import com.goran.aitouristagent.domain.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    tripId: String,
    onBack: () -> Unit,
    onOpenItinerary: () -> Unit,
    onOpenChat: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel(),
) {
    val trip by viewModel.trip.collectAsState()
    val budgetItems by viewModel.budgetItems.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    val planned = budgetItems.sumOf { it.planned }
    val spent = expenses.sumOf { it.amount }
    val budgetTotal = trip?.budgetTotal ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Буџет") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenItinerary) {
                        Icon(Icons.Filled.Event, contentDescription = "Итинерар")
                    }
                    IconButton(onClick = onOpenChat) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI Chat")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SummaryCard(budgetTotal = budgetTotal, planned = planned, spent = spent, currency = trip?.currency ?: "EUR")
            BudgetItemsSection(
                items = budgetItems,
                currency = trip?.currency ?: "EUR",
                onAdd = viewModel::addBudgetItem,
                onDelete = viewModel::deleteBudgetItem,
            )
            ExpensesSection(
                expenses = expenses,
                currency = trip?.currency ?: "EUR",
                onAdd = viewModel::addExpense,
                onDelete = viewModel::deleteExpense,
            )
        }
    }
}

@Composable
private fun SummaryCard(budgetTotal: Double, planned: Double, spent: Double, currency: String) {
    val remaining = budgetTotal - spent
    val overBudget = remaining < 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = "Преглед на буџет", style = MaterialTheme.typography.titleLarge)
        SummaryRow("Вкупен буџет", budgetTotal, currency)
        SummaryRow("Планирано", planned, currency)
        SummaryRow("Потрошено", spent, currency, color = MaterialTheme.colorScheme.secondary)
        SummaryRow(
            label = if (overBudget) "Пречекорено" else "Преостанато",
            amount = if (overBudget) -remaining else remaining,
            currency = currency,
            color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double,
    currency: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "%.2f %s".format(amount, currency),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
        )
    }
}

@Composable
private fun BudgetItemsSection(
    items: List<BudgetItem>,
    currency: String,
    onAdd: (label: String, emoji: String, planned: Double) -> Unit,
    onDelete: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Буџет план", style = MaterialTheme.typography.titleLarge)
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "${item.emoji} ${item.label}", modifier = Modifier.weight(1f))
                Text(text = "%.2f %s".format(item.planned, currency))
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Избриши", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        TextButton(onClick = { showAddDialog = true }) {
            Text("+ Додади буџетска ставка")
        }
    }

    if (showAddDialog) {
        AddBudgetItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { label, emoji, planned ->
                onAdd(label, emoji, planned)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun ExpensesSection(
    expenses: List<Expense>,
    currency: String,
    onAdd: (date: String, category: String, label: String, amount: Double) -> Unit,
    onDelete: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Реални трошоци", style = MaterialTheme.typography.titleLarge)
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) "Собери" else "Прошири",
            )
        }
        if (expanded) {
            expenses.forEach { expense ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = expense.label)
                        Text(
                            text = "${expense.date} · ${expense.category}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(text = "%.2f %s".format(expense.amount, currency))
                    IconButton(onClick = { onDelete(expense.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Избриши", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            TextButton(onClick = { showAddDialog = true }) {
                Text("+ Внеси трошок")
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { date, category, label, amount ->
                onAdd(date, category, label, amount)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddBudgetItemDialog(
    onDismiss: () -> Unit,
    onAdd: (label: String, emoji: String, planned: Double) -> Unit,
) {
    var label by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var planned by remember { mutableStateOf("") }
    val plannedValue = planned.toDoubleOrNull()
    val isValid = label.isNotBlank() && plannedValue != null && plannedValue >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова буџетска ставка") },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Ставка") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it },
                    label = { Text("Емоџи (опционално)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = planned,
                    onValueChange = { planned = it },
                    label = { Text("Планирано") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = { onAdd(label.trim(), emoji.trim().ifBlank { "💶" }, plannedValue ?: 0.0) },
            ) { Text("Додади") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Откажи") }
        },
    )
}

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (date: String, category: String, label: String, amount: Double) -> Unit,
) {
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull()
    val isValid = label.isNotBlank() && date.isNotBlank() && amountValue != null && amountValue >= 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нов трошок") },
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
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категорија (опционално)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Опис") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Износ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    onAdd(date.trim(), category.trim().ifBlank { "Друго" }, label.trim(), amountValue ?: 0.0)
                },
            ) { Text("Додади") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Откажи") }
        },
    )
}
