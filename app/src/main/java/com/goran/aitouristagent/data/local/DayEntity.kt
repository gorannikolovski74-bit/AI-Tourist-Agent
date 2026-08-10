package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.Day

@Entity(
    tableName = "days",
    indices = [Index("tripId")],
)
data class DayEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val date: String,
    val title: String,
    val subtitle: String,
    val orderIndex: Int,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

fun DayEntity.toDomain() = Day(
    id = id,
    tripId = tripId,
    date = date,
    title = title,
    subtitle = subtitle,
    orderIndex = orderIndex,
    updatedAt = updatedAt,
)
