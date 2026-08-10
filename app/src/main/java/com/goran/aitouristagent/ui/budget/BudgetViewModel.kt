package com.goran.aitouristagent.ui.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goran.aitouristagent.data.repository.BudgetRepository
import com.goran.aitouristagent.data.repository.TripRepository
import com.goran.aitouristagent.domain.BudgetItem
import com.goran.aitouristagent.domain.Expense
import com.goran.aitouristagent.domain.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    tripRepository: TripRepository,
) : ViewModel() {
    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    val trip: StateFlow<Trip?> = tripRepository.observeTrips()
        .map { trips -> trips.find { it.id == tripId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val budgetItems: StateFlow<List<BudgetItem>> = budgetRepository.observeBudgetItems(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expenses: StateFlow<List<Expense>> = budgetRepository.observeExpenses(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addBudgetItem(label: String, emoji: String, planned: Double) {
        viewModelScope.launch { budgetRepository.addBudgetItem(tripId, label, emoji, planned) }
    }

    fun deleteBudgetItem(itemId: String) {
        viewModelScope.launch { budgetRepository.deleteBudgetItem(itemId) }
    }

    fun addExpense(date: String, category: String, label: String, amount: Double) {
        viewModelScope.launch { budgetRepository.addExpense(tripId, date, category, label, amount) }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch { budgetRepository.deleteExpense(expenseId) }
    }
}
