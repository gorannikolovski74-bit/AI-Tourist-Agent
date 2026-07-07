package com.goran.aitouristagent.di

import android.content.Context
import androidx.room.Room
import com.goran.aitouristagent.data.local.ActivityDao
import com.goran.aitouristagent.data.local.AppDatabase
import com.goran.aitouristagent.data.local.BudgetItemDao
import com.goran.aitouristagent.data.local.DayDao
import com.goran.aitouristagent.data.local.ExpenseDao
import com.goran.aitouristagent.data.local.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ai-tourist-agent.db")
            // Pre-release app, no installed base to migrate yet.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTripDao(database: AppDatabase): TripDao = database.tripDao()

    @Provides
    fun provideDayDao(database: AppDatabase): DayDao = database.dayDao()

    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao = database.activityDao()

    @Provides
    fun provideBudgetItemDao(database: AppDatabase): BudgetItemDao = database.budgetItemDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()
}
