package com.goran.aitouristagent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TripEntity::class, DayEntity::class, ActivityEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun activityDao(): ActivityDao
}
