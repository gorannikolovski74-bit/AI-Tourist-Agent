package com.goran.aitouristagent.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goran.aitouristagent.data.repository.TripRepository
import com.goran.aitouristagent.domain.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
) : ViewModel() {
    val trips: StateFlow<List<Trip>> = tripRepository.observeTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { tripRepository.refreshFromServer() }
    }

    fun createTrip(
        name: String,
        destination: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        currency: String,
        budgetTotal: Double,
    ) {
        viewModelScope.launch {
            tripRepository.createTrip(
                name = name,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                travelers = travelers,
                currency = currency,
                budgetTotal = budgetTotal,
            )
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch { tripRepository.deleteTrip(tripId) }
    }
}
