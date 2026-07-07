package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.Trip

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val travelers: Int,
    val currency: String,
    val budgetTotal: Double,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

fun TripEntity.toDomain() = Trip(
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

fun Trip.toEntity() = TripEntity(
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
