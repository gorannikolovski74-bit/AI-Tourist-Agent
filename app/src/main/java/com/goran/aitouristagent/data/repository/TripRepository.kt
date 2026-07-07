package com.goran.aitouristagent.data.repository

import com.goran.aitouristagent.data.local.TripDao
import com.goran.aitouristagent.data.local.toDomain
import com.goran.aitouristagent.domain.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
) {
    fun observeTrips(): Flow<List<Trip>> =
        tripDao.observeTrips().map { entities -> entities.map { it.toDomain() } }
}
