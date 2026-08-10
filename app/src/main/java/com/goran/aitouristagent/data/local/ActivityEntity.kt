package com.goran.aitouristagent.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goran.aitouristagent.domain.Activity

@Entity(
    tableName = "activities",
    indices = [Index("dayId")],
)
data class ActivityEntity(
    @PrimaryKey val id: String,
    val dayId: String,
    val text: String,
    val price: Double?,
    val mapsUrl: String?,
    val orderIndex: Int,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

fun ActivityEntity.toDomain() = Activity(
    id = id,
    dayId = dayId,
    text = text,
    price = price,
    mapsUrl = mapsUrl,
    orderIndex = orderIndex,
    updatedAt = updatedAt,
)
