package com.goran.aitouristagent.data.repository

import android.util.Log
import com.goran.aitouristagent.data.local.TripDao
import com.goran.aitouristagent.data.local.TripEntity
import com.goran.aitouristagent.data.local.toDomain
import com.goran.aitouristagent.data.local.toEntity
import com.goran.aitouristagent.data.remote.ApiService
import com.goran.aitouristagent.data.remote.TripDto
import com.goran.aitouristagent.domain.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TripRepository"

@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val apiService: ApiService,
) {
    fun observeTrips(): Flow<List<Trip>> =
        tripDao.observeTrips().map { entities -> entities.map { it.toDomain() } }

    suspend fun createTrip(
        name: String,
        destination: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        currency: String,
        budgetTotal: Double,
    ): Trip {
        val trip = Trip(
            id = UUID.randomUUID().toString(),
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            travelers = travelers,
            currency = currency,
            budgetTotal = budgetTotal,
            updatedAt = System.currentTimeMillis(),
        )
        tripDao.upsert(trip.toEntity())
        runCatching { apiService.createTrip(trip.toDto()) }
            .onFailure { Log.w(TAG, "createTrip: server sync failed, kept locally", it) }
        return trip
    }

    suspend fun updateTrip(trip: Trip) {
        val updated = trip.copy(updatedAt = System.currentTimeMillis())
        tripDao.upsert(updated.toEntity())
        runCatching { apiService.updateTrip(updated.id, updated.toDto()) }
            .onFailure { Log.w(TAG, "updateTrip: server sync failed, kept locally", it) }
    }

    suspend fun deleteTrip(tripId: String) {
        tripDao.markDeleted(tripId, System.currentTimeMillis())
        runCatching { apiService.deleteTrip(tripId) }
            .onFailure { Log.w(TAG, "deleteTrip: server sync failed, kept locally", it) }
    }

    /** Pulls trips from the server and merges them in, last-write-wins by [Trip.updatedAt]. */
    suspend fun refreshFromServer() {
        val remoteTrips = runCatching { apiService.getTrips() }
            .onFailure { Log.w(TAG, "refreshFromServer: fetch failed", it) }
            .getOrNull() ?: return

        for (dto in remoteTrips) {
            val local = tripDao.getTripById(dto.id)
            if (local == null || dto.updatedAt > local.updatedAt) {
                tripDao.upsert(dto.toEntity())
            }
        }
    }
}

private fun Trip.toDto() = TripDto(
    id = id,
    name = name,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    travelers = travelers,
    currency = currency,
    budgetTotal = budgetTotal,
    updatedAt = updatedAt,
)

private fun TripDto.toEntity() = TripEntity(
    id = id,
    name = name,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    travelers = travelers,
    currency = currency,
    budgetTotal = budgetTotal,
    updatedAt = updatedAt,
)
