package com.goran.aitouristagent.ui.itinerary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goran.aitouristagent.data.repository.ItineraryRepository
import com.goran.aitouristagent.data.repository.TripRepository
import com.goran.aitouristagent.domain.Activity
import com.goran.aitouristagent.domain.Day
import com.goran.aitouristagent.domain.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itineraryRepository: ItineraryRepository,
    tripRepository: TripRepository,
) : ViewModel() {
    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    val trip: StateFlow<Trip?> = tripRepository.observeTrips()
        .map { trips -> trips.find { it.id == tripId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val days: StateFlow<List<Day>> = itineraryRepository.observeDays(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observeActivities(dayId: String): Flow<List<Activity>> =
        itineraryRepository.observeActivities(dayId)

    fun addDay(date: String, title: String, subtitle: String) {
        viewModelScope.launch { itineraryRepository.addDay(tripId, date, title, subtitle) }
    }

    fun deleteDay(dayId: String) {
        viewModelScope.launch { itineraryRepository.deleteDay(dayId) }
    }

    fun addActivity(dayId: String, text: String, price: Double?, mapsUrl: String?) {
        viewModelScope.launch { itineraryRepository.addActivity(dayId, text, price, mapsUrl) }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch { itineraryRepository.deleteActivity(activityId) }
    }
}
