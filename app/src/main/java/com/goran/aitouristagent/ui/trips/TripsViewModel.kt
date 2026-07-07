package com.goran.aitouristagent.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goran.aitouristagent.data.repository.TripRepository
import com.goran.aitouristagent.domain.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    tripRepository: TripRepository,
) : ViewModel() {
    val trips: StateFlow<List<Trip>> = tripRepository.observeTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
